/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.entrypoint;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.clusterframework.FlinkResourceManager;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.entrypoint.parser.CommandLineParser;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.metrics.MetricRegistry;
import org.apache.flink.runtime.resourcemanager.*;
import org.apache.flink.runtime.rpc.FatalErrorHandler;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.util.EnvironmentInformation;
import org.apache.flink.runtime.util.JvmShutdownSafeguard;
import org.apache.flink.runtime.util.SignalHandler;

import javax.annotation.Nullable;

/**
 * Entry point for the standalone session cluster.
 *
 * 独立会话集群的入口点
 */
public class StandaloneSessionClusterEntrypoint extends SessionClusterEntrypoint {

	public StandaloneSessionClusterEntrypoint(Configuration configuration) {
		super(configuration);
	}

	@Override
	protected ResourceManager<?> createResourceManager(
			Configuration configuration,
			ResourceID resourceId,
			RpcService rpcService,
			HighAvailabilityServices highAvailabilityServices,
			HeartbeatServices heartbeatServices,
			MetricRegistry metricRegistry,
			FatalErrorHandler fatalErrorHandler,
			ClusterInformation clusterInformation,
			@Nullable String webInterfaceUrl) throws Exception {
		final ResourceManagerConfiguration resourceManagerConfiguration = ResourceManagerConfiguration.fromConfiguration(configuration);
		final ResourceManagerRuntimeServicesConfiguration resourceManagerRuntimeServicesConfiguration = ResourceManagerRuntimeServicesConfiguration.fromConfiguration(configuration);
		final ResourceManagerRuntimeServices resourceManagerRuntimeServices = ResourceManagerRuntimeServices.fromConfiguration(
			resourceManagerRuntimeServicesConfiguration,
			highAvailabilityServices,
			rpcService.getScheduledExecutor());

		return new StandaloneResourceManager(
			rpcService,
			FlinkResourceManager.RESOURCE_MANAGER_NAME,
			resourceId,
			resourceManagerConfiguration,
			highAvailabilityServices,
			heartbeatServices,
			resourceManagerRuntimeServices.getSlotManager(),
			metricRegistry,
			resourceManagerRuntimeServices.getJobLeaderIdService(),
			clusterInformation,
			fatalErrorHandler);
	}

	public static void main(String[] args) {
		// startup checks and logging
		EnvironmentInformation.logEnvironmentInfo(LOG, StandaloneSessionClusterEntrypoint.class.getSimpleName(), args);
		SignalHandler.register(LOG);
		JvmShutdownSafeguard.installAsShutdownHook(LOG);

		EntrypointClusterConfiguration entrypointClusterConfiguration = null;
		final CommandLineParser<EntrypointClusterConfiguration> commandLineParser = new CommandLineParser<>(new EntrypointClusterConfigurationParserFactory());

		try {
			// /Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/bin/java  -Xms1024m -Xmx1024m
			// -Dlog.file=/Users/wyd/soft/flink-1.6.0/log/flink-wyd-standalonesession-0-wangyandong.local.log
			// -Dlog4j.configuration=file:/Users/wyd/soft/flink-1.6.0/conf/log4j.properties -Dlogback.configurationFile=file:/Users/wyd/soft/flink-1.6.0/conf/logback.xml
			// -classpath /Users/wyd/soft/flink-1.6.0/lib/flink-python_2.11-1.6.0.jar:/Users/wyd/soft/flink-1.6.0/lib/log4j-1.2.17.jar:/Users/wyd/soft/flink-1.6.0/lib/slf4j-log4j12-1.7.7.jar:
			// /Users/wyd/soft/flink-1.6.0/lib/flink-dist_2.11-1.6.0.jar::/Users/wyd/soft/hadoop-2.8.4/etc/hadoop:
			// org.apache.flink.runtime.entrypoint.StandaloneSessionClusterEntrypoint --configDir /Users/wyd/soft/flink-1.6.0/conf
			// --executionMode cluster > /Users/wyd/soft/flink-1.6.0/log/flink-wyd-standalonesession-0-wangyandong.local.out 200<&- 2>&1 < /dev/null &
			// 解析上面的命令行
			entrypointClusterConfiguration = commandLineParser.parse(args);
		} catch (FlinkParseException e) {
			LOG.error("Could not parse command line arguments {}.", args, e);
			commandLineParser.printHelp();
			System.exit(1);
		}

		Configuration configuration = loadConfiguration(entrypointClusterConfiguration);

		StandaloneSessionClusterEntrypoint entrypoint = new StandaloneSessionClusterEntrypoint(configuration);

		// 启动集群
		entrypoint.startCluster();
	}
}

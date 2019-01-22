# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
	. ~/.bashrc
fi

# User specific environment and startup programs

PATH=$PATH:$HOME/bin

export PATH

export HBASE_HOME=/mnt/opt/hbase-2.1.2
export HBASE_CONF_DIR=$HBASE_HOME/conf
export HBASE_CLASS_PATH=HADOOP_CONF_DIR
#export HBASE_CLASS_PATH=$HBASE_CONF_DIR
export PATH=$PATH:$HBASE_HOME/bin

export HADOOP_CONF_DIR=/mnt/opt/hadoop-2.8.4/etc/hadoop
export PATH=$PATH:$HADOOP_CONF_DIR/bin
export HADOOP_USER_NAME=hadoop
export HADOOP_HOME=/mnt/opt/hadoop-2.8.4
export PATH=$PATH:$HADOOP_HOME/bin

export HBASE_LIBRARY_PATH=$HBASE_LIBRARY_PATH:$HBASE_HOME/lib/native/Linux-amd64-64/:/usr/local/lib/

package wang.xiaoluobo.webwar.page;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Page<T> implements Serializable {

    private int pageIndex = 1;
    private int pageSize = 10;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 总记录数
     */
    private int totalCounts;

    /**
     * true:需要分页的地方，传入的参数就是Page实体
     * false:需要分页的地方，传入的参数所代表的实体拥有Page属性
     */
    private boolean entityOrField;

    /**
     * 查询结果
     */
    private Object data;

    /**
     * 分页查询时，附带实体赋值给param对象
     */
    private Object param;

    /**
     * 分页查询时，附带参数赋值给map对象
     */
    private Map<String, Object> map = new HashMap<String, Object>();

    public int getPageIndex() {
        if (pageIndex <= 0) {
            pageIndex = 1;
        }

        if (pageIndex > totalPages) {
            pageIndex = totalPages;
        }
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        if (totalCounts % totalPages == 0) {
            totalPages = totalCounts / totalPages;
        } else {
            totalPages = totalCounts / totalPages + 1;
        }
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    public boolean isEntityOrField() {
        return entityOrField;
    }

    public void setEntityOrField(boolean entityOrField) {
        this.entityOrField = entityOrField;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}

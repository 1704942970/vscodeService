package com.kj.vscode.web.base;


/**
 * 
 * 分页查询的参数
 * 
 * @author zhouz
 *
 */
public class PageBean extends BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer pageSize;
	private Integer pageIndex;
	private Integer totalCount;
	private Integer pageCount;

	public PageBean() {
	}

	public PageBean(int pageSize, int pageIndex,int totalCount,int pageCount) {
		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
		this.totalCount = totalCount;
		this.pageCount = pageCount;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	@Override
	public String toString() {
		return "PageVo [pageSize=" + pageSize + ", pageIndex=" + pageIndex + ", totalCount=" + totalCount
				+ ", pageCount=" + pageCount + "]";
	}


}

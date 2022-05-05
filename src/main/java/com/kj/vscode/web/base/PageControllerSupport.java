package com.kj.vscode.web.base;

/**
 * 提供分页功能帮助功能的抽象类，定义了一些方法
 * 
 * @author zhouz
 */
public abstract class PageControllerSupport extends BaseController{
	protected static final int DEFAULT_PAGE_SIZE = 10;
	protected static final int DEFAULT_PAGE_INDEX = 1;

	/**
	 * 可以定制的pageSize
	 */
	protected Integer customPageSize = null;

	/**
	 * 填充默认的分页参数信息。包括 pageIndex 和 pageSize;
	 * 
	 * @return
	 */
	protected PageBean fillDefaultPage(PageBean page) {
		if (null == page) {
			return null;
		} else {
			if (null == page.getPageIndex())
				page.setPageIndex(DEFAULT_PAGE_INDEX);
			if (null == page.getPageSize())
				page.setPageSize(customPageSize == null ? DEFAULT_PAGE_SIZE : customPageSize);
		}
		return page;
	}

	/**
	 * 根据转入的PageVo对象 和 totalCount ， 计算PageVo对象内容的分页总数pageCount和totalCount。
	 * 同时，检查pageVo对象中的参数是否合法，若不合法，则填充为默认值
	 * 
	 * @param page
	 * @param totalSize 记录总数
	 * @return
	 */
	protected PageBean formatePageVo(PageBean page, int totalCount) {
		if (null == page)
			return page;

		if (page.getPageSize() <= 0) {
			page.setPageSize(customPageSize == null ? DEFAULT_PAGE_SIZE : customPageSize);
		}
		if (page.getPageIndex() <= 0) {
			page.setPageIndex(DEFAULT_PAGE_INDEX);
		}
		// 总数
		page.setTotalCount(totalCount);
		// 分页总数pageCount
		int pageCount = (int) Math.ceil(((double) totalCount) / page.getPageSize());
		page.setPageCount(pageCount);

		if (page.getPageIndex() > page.getPageCount()) {
			page.setPageIndex(page.getPageCount());
		}
		return page;
	}

}

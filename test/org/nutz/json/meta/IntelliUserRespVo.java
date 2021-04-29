package org.nutz.json.meta;

import java.util.List;

public class IntelliUserRespVo {
    private List<IntellifUserAuthVo> data;
    private Integer errCode;
    private Integer maxPage;
    private Integer total;
    private Integer count;
	public List<IntellifUserAuthVo> getData() {
		return data;
	}
	public void setData(List<IntellifUserAuthVo> data) {
		this.data = data;
	}
	public Integer getErrCode() {
		return errCode;
	}
	public void setErrCode(Integer errCode) {
		this.errCode = errCode;
	}
	public Integer getMaxPage() {
		return maxPage;
	}
	public void setMaxPage(Integer maxPage) {
		this.maxPage = maxPage;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
    
}
 
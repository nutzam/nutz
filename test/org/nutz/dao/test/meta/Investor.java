package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.mvc.annotation.Param;

/**
 *
 * @author 帅
 */
@Table("investor")
public class Investor {

    @Param("investorId")
    @Column("id")
    private int id;

    @Id(auto = false)
    private int user_id;

    //个人还是机构
    @Column("is_personal")
    private int isPersonal;

    //详细信息
    @Column("detail")
    private String detail;

    //名片
    @Column("business_card_img")
    private String businessCardImg;

    //身份证
    @Column("id_card_img")
    private String idCardImg;

    //资产证明
    @Column("asset_proof")
    private String assetProof;

    //推荐人id
    @Column("referee_id")
    private int refereeId;

    //机构名称
    @Column("institution_name")
    private String institutionName;

    //资产
    private String[] investorAssetsIds;

    //职位
    private String[] investorPositionIds;

    //个人收入
    @Column("personal_income")
    private int personalIncome;

    //证券资产
    @Column("securities_assets")
    private int securitiesAssets;

    //路演规模
    @Column("roadshow_type")
    private int roadshowType;

    //路演频率
    @Column("roadshow_frequency")
    private int roadshowFrequency;

    //计划投资公司数
    @Column("invest_company_num")
    private int investCompanyNum;

    //投资实力
    @Column("invest_strength")
    private int investStrength;

    //有无成功投资经历
    @Column("invset_successed")
    private int invsetSuccessed;

    //公司名称
    private String[] companyName;

    //上市情况
    private String[] stockMarket;

    //如何帮助创业者
    @Column("how_help")
    private String howHelp;

    //是否影子数据
    @Column("is_shadow")
    private int isShadow;

    //是否高校导师
    @Column("status")
    private int status;

    //是否高校导师
    @Column("status_name")
    private String statusName;

    //投资人类型
    private String[] investorTypeIds;

    private String[] investorTradeIds;

    private String[] investorRoundIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(int isPersonal) {
        this.isPersonal = isPersonal;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getBusinessCardImg() {
        return businessCardImg;
    }

    public void setBusinessCardImg(String businessCardImg) {
        this.businessCardImg = businessCardImg;
    }

    public String getIdCardImg() {
        return idCardImg;
    }

    public void setIdCardImg(String idCardImg) {
        this.idCardImg = idCardImg;
    }


    public String getAssetProof() {
        return assetProof;
    }

    public void setAssetProof(String assetProof) {
        this.assetProof = assetProof;
    }

    public int getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(int refereeId) {
        this.refereeId = refereeId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String[] getInvestorAssetsIds() {
        return investorAssetsIds;
    }

    public void setInvestorAssetsIds(String[] investorAssetsIds) {
        this.investorAssetsIds = investorAssetsIds;
    }

    public String[] getInvestorPositionIds() {
        return investorPositionIds;
    }

    public void setInvestorPositionIds(String[] investorPositionIds) {
        this.investorPositionIds = investorPositionIds;
    }

    public int getPersonalIncome() {
        return personalIncome;
    }

    public void setPersonalIncome(int personalIncome) {
        this.personalIncome = personalIncome;
    }

    public int getSecuritiesAssets() {
        return securitiesAssets;
    }

    public void setSecuritiesAssets(int securitiesAssets) {
        this.securitiesAssets = securitiesAssets;
    }

    public int getRoadshowType() {
        return roadshowType;
    }

    public void setRoadshowType(int roadshowType) {
        this.roadshowType = roadshowType;
    }

    public int getRoadshowFrequency() {
        return roadshowFrequency;
    }

    public void setRoadshowFrequency(int roadshowFrequency) {
        this.roadshowFrequency = roadshowFrequency;
    }

    public int getInvestCompanyNum() {
        return investCompanyNum;
    }

    public void setInvestCompanyNum(int investCompanyNum) {
        this.investCompanyNum = investCompanyNum;
    }

    public int getInvestStrength() {
        return investStrength;
    }

    public void setInvestStrength(int investStrength) {
        this.investStrength = investStrength;
    }

    public int getInvsetSuccessed() {
        return invsetSuccessed;
    }

    public void setInvsetSuccessed(int invsetSuccessed) {
        this.invsetSuccessed = invsetSuccessed;
    }

    public String[] getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String[] companyName) {
        this.companyName = companyName;
    }

    public String[] getStockMarket() {
        return stockMarket;
    }

    public void setStockMarket(String[] stockMarket) {
        this.stockMarket = stockMarket;
    }

    public String getHowHelp() {
        return howHelp;
    }

    public void setHowHelp(String howHelp) {
        this.howHelp = howHelp;
    }

    public int getIsShadow() {
        return isShadow;
    }

    public void setIsShadow(int isShadow) {
        this.isShadow = isShadow;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String[] getInvestorTypeIds() {
        return investorTypeIds;
    }

    public void setInvestorTypeIds(String[] investorTypeIds) {
        this.investorTypeIds = investorTypeIds;
    }

    public String[] getInvestorTradeIds() {
        return investorTradeIds;
    }

    public void setInvestorTradeIds(String[] investorTradeIds) {
        this.investorTradeIds = investorTradeIds;
    }

    public String[] getInvestorRoundIds() {
        return investorRoundIds;
    }

    public void setInvestorRoundIds(String[] investorRoundIds) {
        this.investorRoundIds = investorRoundIds;
    }

}
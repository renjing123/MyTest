package cc.joymaker.utils;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import cc.joymaker.weiop.open.base.model.Region;
import cc.joymaker.weiop.open.base.service.RegionService;
import cc.joymaker.weiop.open.base.service.impl.RegionServiceImpl;
import cc.joymaker.weiop.open.base.utils.DateUtils;

public class LogUtils {

	private static Logger classLog = LoggerFactory.getLogger(LogUtils.class);
	private static Logger log = LoggerFactory.getLogger("log.audit");

	private static ForkJoinPool executor = ForkJoinPool.commonPool();

	private static RegionService regionService;

	public final class BusinessId {
		public final static String QR = "qr";
		public final static String ORDER = "order";
		public final static String PRODUCT = "product";
		public final static String SKU = "sku";
		public final static String USER = "user";
	}

	private static void init() {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		if (regionService == null && context != null) {
			regionService = (RegionService) context.getBean(RegionServiceImpl.class);
		}
	}

	/**
	 * 
	 * @param stime
	 *            时间
	 * @param orgId
	 *            企业号
	 * @param businessId
	 *            业务编码
	 * @param moduleId
	 *            业务内模块编码
	 * @param opType
	 *            操作类型
	 * @param uid
	 *            用户编码
	 * @param utype
	 *            用户类型,1:普通用户，2:管理员
	 * @param userRole
	 *            用户角色
	 * @param mobile
	 *            手机号
	 * @param openid
	 *            微信openid
	 * @param channelType
	 *            操作渠道来源
	 * @param channelID
	 *            操作来源名称值
	 * @param action
	 *            行为说明
	 * @param memo
	 *            其它扩展数据（json格式存储）
	 * @param ip
	 *            用户ip
	 * @param lat
	 *            坐标纬度
	 * @param lng
	 *            坐标经度
	 * @param province
	 *            省
	 * @param city
	 *            市
	 * @param district
	 *            区
	 */
	public static void audit(final Date stime, final String orgId, final String businessId, final String moduleId,
			final String opType, final String uid, final int utype, final int userRole, final String mobile,
			final String openid, final String channelType, final String channelID, final String action,
			final String memo, final String serverIP, final String clientIP, final Double lat, final Double lng) {
		init();
		executor.execute(() -> {
			String _lat = "", _lng = "", _provinceCode = "", _cityCode = "", _districtCode = "", _province = "",
					_city = "", _district = "";

			if ((lat == null || lng == null) || (lat == -1 || lng == -1) || (lat == 0.0 || lng == 0.0)) {
				if (StringUtils.isNotEmpty(clientIP)) {

					Region district = regionService.getRegion(clientIP);
					if (district != null) {
						_lat = district.getLat().toString();
						_lng = district.getLng().toString();

						_district = district.getShortName();
						_districtCode = district.getCode();

						Region city = regionService.getCity(district.getCode().toString());
						if (city != null) {
							_cityCode = city.getCode();
							_city = city.getShortName();

							Region province = regionService.getProvince(city.getCode().toString());
							if (province != null) {
								_provinceCode = province.getCode();
								_province = province.getShortName();
							}
						}
					}
				}
			} else {
				_lat = lat.toString();
				_lng = lng.toString();
				Region district = regionService.getRegion(lat, lng);
				// log.info("------" + district.getName() + "," + lat + ","
				// + "lng" + "," + district.getCode());

				if (district != null) {
					_district = district.getShortName();
					_districtCode = district.getCode();

					Region city = regionService.getCity(district.getCode().toString());
					if (city != null) {
						_cityCode = city.getCode();
						_city = city.getShortName();

						Region province = regionService.getProvince(city.getCode().toString());
						if (province != null) {
							_provinceCode = province.getCode();
							_province = province.getShortName();
						}
					}
				}
			}

			String spt = "\t";
			String content = new StringBuffer(DateUtils.parse(stime)).append(spt).append(orgId).append(spt)
					.append(businessId).append(spt).append(moduleId).append(spt).append(opType).append(spt).append(uid)
					.append(spt).append(utype).append(spt).append(userRole).append(spt).append(mobile).append(spt)
					.append(openid).append(spt).append(channelType).append(spt).append(channelID).append(spt)
					.append(action).append(spt).append(memo).append(spt).append(serverIP).append(spt).append(clientIP)
					.append(spt).append(_lat).append(spt).append(_lng).append(spt).append(_provinceCode).append(spt)
					.append(_cityCode).append(spt).append(_districtCode).append(spt).append(_province).append(spt)
					.append(_city).append(spt).append(_district).toString();
			log.info(content);

		});

	}

	/**
	 * 
	 * @param requestId
	 *            请求id
	 * @param stime
	 *            请求时间
	 * @param appid
	 *            分配appid
	 * @param orgId
	 *            企业id
	 * @param moduleId
	 *            模块名称
	 * @param interfaceName
	 *            接口名称
	 * @param method
	 *            请求方法
	 * @param headers
	 *            请求头
	 * @param params
	 *            请求参数
	 * @param code
	 *            返回code
	 * @param msg
	 *            返回msg
	 * @param result
	 *            返回内容
	 * @param ip
	 *            clientip
	 */

	public static void request(final String requestId, final Date stime, final String appid, final String orgId,
			final String uri, final String moduleId, final String interfaceName, final String method,
			final String headers, final String params, final String body, final int code, final String msg,
			final String result, final String serverIP, final String clientIP, final Double lat, final Double lng) {
		init();
		executor.execute(() -> {

			String _lat = "", _lng = "", _provinceCode = "", _cityCode = "", _districtCode = "", _province = "",
					_city = "", _district = "";
			try {

				if ((lat == null || lng == null) || (lat == -1 || lng == -1) || (lat == 0.0 || lng == 0.0)) {
					if (StringUtils.isNotEmpty(clientIP)) {

						Region district = regionService.getRegion(clientIP);
						if (district != null) {
							_lat = district.getLat().toString();
							_lng = district.getLng().toString();

							_district = district.getShortName();
							_districtCode = district.getCode();

							Region city = regionService.getCity(district.getCode().toString());
							if (city != null) {
								_cityCode = city.getCode();
								_city = city.getShortName();

								Region province = regionService.getProvince(city.getCode().toString());
								if (province != null) {
									_provinceCode = province.getCode();
									_province = province.getShortName();
								}
							}
						}
					}
				} else {
					_lat = lat.toString();
					_lng = lng.toString();
					Region district = regionService.getRegion(lat, lng);
					// log.info("------" + district.getName() + "," + lat +
					// ","
					// + "lng" + "," + district.getCode());

					if (district != null) {
						_district = district.getShortName();
						_districtCode = district.getCode();

						Region city = regionService.getCity(district.getCode().toString());
						if (city != null) {
							_cityCode = city.getCode();
							_city = city.getShortName();

							Region province = regionService.getProvince(city.getCode().toString());
							if (province != null) {
								_provinceCode = province.getCode();
								_province = province.getShortName();
							}
						}
					}
				}
			} catch (Exception ex) {
				classLog.error(ex.getMessage(), ex);
			}

			String spt = "\t";
			String content = new StringBuffer().append(DateUtils.parse(stime)).append(spt).append(requestId).append(spt)
					.append(orgId).append(spt).append(uri).append(spt).append(moduleId).append(spt)
					.append(interfaceName).append(spt).append(method).append(spt).append(headers).append(spt)
					.append(params).append(spt).append(body).append(spt).append(code).append(spt).append(msg)
					.append(spt).append(result).append(spt).append(serverIP).append(spt).append(clientIP).append(spt)
					.append(_lat).append(spt).append(_lng).append(spt).append(_provinceCode).append(spt)
					.append(_cityCode).append(spt).append(_districtCode).append(spt).append(_province).append(spt)
					.append(_city).append(spt).append(_district).toString();
			log.info(content);

		});
	}

}

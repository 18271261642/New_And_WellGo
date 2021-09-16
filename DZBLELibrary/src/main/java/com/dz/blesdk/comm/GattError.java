 
package com.dz.blesdk.comm;

/**
 * 作者:东芝(2017/11/18).
 * 蓝牙底层错误常量.和具体原因
 */
public  class GattError {

	/**
	 * 连接超时,通常是设备不在范围内,或距离设备太远,或设备程序故障,或Android系统蓝牙兼容问题
	 */
	public static final int ERROR_TIMEOUT_CONNECT=-1;
	/**
	 * 发现服务超时,通常是距离设备太远 或设备程序故障 或Android系统蓝牙兼容问题(关闭蓝牙再打开 或重启手机解决)
	 */
	public static final int ERROR_TIMEOUT_DISCOVERSERVICES=-2;
	/**
	 * 通知开启超时,通常是距离设备太远 或设备程序故障 或系统所支持的通知开启数量超过了最大值
	 */
	public static final int ERROR_TIMEOUT_NOTIFY_ENABLE=-3;
	/**
	 * 发现服务错误,通常是设备灾难性故障
	 */
	public static final int ERROR_SERVICES_DISCOVERED_ERROR=-100;
	/**
	 * 通知请求开启错误,通常是UUID写错,或设备不支持这个通知特性
	 */
	public static final int ERROR_SERVICES_NOTIFY_ERROR=-101;
	/**
	 * 连接失败,未知错误 通常是 -192 -133 -8 -64等 具体请使用parse() 方法来解析答案
	 * 具体原因是兼容性导致 解决办法是重启手机或关闭打开蓝牙, 本框架做的处理是直接断开蓝牙
	 */
	private static final int ERROR_UNKNOWN =-102;

 

	public static String parse(final int error) {
		switch (error) {
		case 0x0001:
			return "GATT INVALID HANDLE";
		case 0x0002:
			return "GATT READ NOT PERMIT";
		case 0x0003:
			return "GATT WRITE NOT PERMIT";
		case 0x0004:
			return "GATT INVALID PDU";
		case 0x0005:
			return "GATT INSUF AUTHENTICATION";
		case 0x0006:
			return "GATT REQ NOT SUPPORTED";
		case 0x0007:
			return "GATT INVALID OFFSET";
		case 0x0008:
			return "GATT INSUF AUTHORIZATION";
		case 0x0009:
			return "GATT PREPARE Q FULL";
		case 0x000a:
			return "GATT NOT FOUND";
		case 0x000b:
			return "GATT NOT LONG";
		case 0x000c:
			return "GATT INSUF KEY SIZE";
		case 0x000d:
			return "GATT INVALID ATTR LEN";
		case 0x000e:
			return "GATT ERR UNLIKELY";
		case 0x000f:
			return "GATT INSUF ENCRYPTION";
		case 0x0010:
			return "GATT UNSUPPORT GRP TYPE";
		case 0x0011:
			return "GATT INSUF RESOURCE";
		case 0x0087:
			return "GATT ILLEGAL PARAMETER";
		case 0x0080:
			return "GATT NO RESOURCES";
		case 0x0081:
			return "GATT INTERNAL ERROR";
		case 0x0082:
			return "GATT WRONG STATE";
		case 0x0083:
			return "GATT DB FULL";
		case 0x0084:
			return "GATT BUSY";
		case 0x0085:
			return "GATT ERROR";
		case 0x0086:
			return "GATT CMD STARTED";
		case 0x0088:
			return "GATT PENDING";
		case 0x0089:
			return "GATT AUTH FAIL";
		case 0x008a:
			return "GATT MORE";
		case 0x008b:
			return "GATT INVALID CFG";
		case 0x008c:
			return "GATT SERVICE STARTED";
		case 0x008d:
			return "GATT ENCRYPED NO MITM";
		case 0x008e:
			return "GATT NOT ENCRYPTED";
		case 0x0101:
			return "TOO MANY OPEN CONNECTIONS";
		case 0x00FF:
			return "DFU SERVICE DISCOVERY NOT STARTED";
		default:
			return "UNKNOWN (" + error + ")";
		}
	}
}

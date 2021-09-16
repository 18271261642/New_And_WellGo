package generalplus.com.GPCamLib;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GPXMLParse {

	public static int CategoryLevel = 12;
	public static int SettingLevel = 6;
	public static int ValueLevel = 0;

	//Item Index
	public static int RecordResolution_Setting_ID = 0x00000000;
	public static int CaptureResolution_Setting_ID = 0x00000100;
	public static int Version_Setting_ID = 0x00000209;
	public static int Version_Value_Index = 0;
	
	private GPXMLValue m_GPXMLValue;
	private GPXMLSetting m_GPXMLSetting;
	private GPXMLCategory m_GPXMLCategory;
	private static ArrayList<GPXMLValue> m_aryListGPXMLValues;
	private static ArrayList<GPXMLSetting> m_aryListGPXMLSettings;
	private static ArrayList<GPXMLCategory> m_aryListGPXMLCategroies = new ArrayList<GPXMLCategory>();
	private static String GPTag = "GPXMLParseLog";

	public GPXMLParse() {
		if (m_aryListGPXMLValues == null)
			m_aryListGPXMLValues = new ArrayList<GPXMLValue>();
		if (m_aryListGPXMLSettings == null)
			m_aryListGPXMLSettings = new ArrayList<GPXMLSetting>();
		if (m_aryListGPXMLCategroies == null)
			m_aryListGPXMLCategroies = new ArrayList<GPXMLCategory>();
	}

	public ArrayList<GPXMLCategory> GetCategories() {
		m_aryListGPXMLValues.clear();
		m_aryListGPXMLSettings.clear();
		m_aryListGPXMLCategroies.clear();

		return m_aryListGPXMLCategroies;
	}

	public class GPXMLValue {
		public String strXMLValueName;
		public String strXMLValueID;
		public int i32TreeLevel;

		public GPXMLValue(String ValueName, String ValueID, int TreeLevel) {
			this.strXMLValueName = ValueName;
			this.strXMLValueID = ValueID;
			this.i32TreeLevel = TreeLevel;
		}
	}

	public class GPXMLSetting {
		public String strXMLSettingName;
		public String strXMLSettingID;
		public String strXMLSettingType;
		public String strXMLSettingReflash;
		public String strXMLSettingDefaultValue;
		public String strXMLSettingCurrent = null;
		public int i32TreeLevel;
		public ArrayList<GPXMLValue> aryListGPXMLValues;

		public GPXMLSetting(String SettingName, String SettingID,
				String SettingType, String SettingReflash, String SettingDefaultValue, int TreeLevel,
				ArrayList<GPXMLValue> XMLValue) {
			this.strXMLSettingName = SettingName;
			this.strXMLSettingID = SettingID;
			this.strXMLSettingType = SettingType;
			this.strXMLSettingReflash = SettingReflash;
			this.strXMLSettingDefaultValue = SettingDefaultValue;
			if (null != XMLValue) {
				for (int i = 0; i < XMLValue.size(); i++) {
					if(XMLValue.get(i).strXMLValueID.equalsIgnoreCase(SettingDefaultValue)) {
						this.strXMLSettingCurrent = XMLValue.get(i).strXMLValueName;
						break;
					}
				}
			}
			
			this.i32TreeLevel = TreeLevel;
			this.aryListGPXMLValues = (ArrayList<GPXMLValue>) XMLValue.clone();
		}
	}

	public class GPXMLCategory {
		public String strXMLCategoryName;
		public int i32TreeLevel;
		public ArrayList<GPXMLSetting> aryListGPXMLSettings;

		public GPXMLCategory(String CategoryName, int TreeLevel,
				ArrayList<GPXMLSetting> XMLSetting) {
			this.strXMLCategoryName = CategoryName;
			this.i32TreeLevel = i32TreeLevel;
			this.aryListGPXMLSettings = (ArrayList<GPXMLSetting>) XMLSetting
					.clone();
		}
	}

	public ArrayList<GPXMLCategory> GetGPXMLInfo(String FilePath) {
		try {
			File xmlFile = new File(FilePath);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);

			// Get Node - Categories
			NodeList nodeList_Categories = doc
					.getElementsByTagName("Categories");

			String strCategoryName = "", strSettingName = "", strSettingID = "", strSettingType = "", strSettingReflash = "", strSettingDefault = "", strValueName = "", strValueID = "", strTemp = "";
			m_aryListGPXMLCategroies.clear();

			for (int i32CategoriesIndex = 0; i32CategoriesIndex < nodeList_Categories
					.getLength(); i32CategoriesIndex++) {
				Node node_Categories = nodeList_Categories
						.item(i32CategoriesIndex);

				if (node_Categories.getNodeType() == Node.ELEMENT_NODE) {
					Element element_Categories = (Element) node_Categories;

					// Get Node- Category
					NodeList nodeList_Category = element_Categories
							.getElementsByTagName("Category");

					for (int i32CategoryIndex = 0; i32CategoryIndex < nodeList_Category
							.getLength(); i32CategoryIndex++) {

						Node node_Category = nodeList_Category
								.item(i32CategoryIndex);

						if (node_Category.getNodeType() == Node.ELEMENT_NODE) {
							Element element_Category = (Element) node_Category;
							m_aryListGPXMLSettings.clear();

							// Get Category Name
							NodeList nodeList_CategoryName = element_Category
									.getElementsByTagName("Name");
							if (nodeList_CategoryName.getLength() > 0) {
								strCategoryName = ((Node) ((Element) nodeList_CategoryName
										.item(0)).getChildNodes().item(0))
										.getNodeValue();
							} else
								strCategoryName = "";

							// Get Node - Settings
							NodeList nodeList_Settings = element_Category
									.getElementsByTagName("Settings");

							for (int i32SettingsIndex = 0; i32SettingsIndex < nodeList_Settings
									.getLength(); i32SettingsIndex++) {
								Node node_Settings = nodeList_Settings
										.item(i32SettingsIndex);
								if (node_Settings.getNodeType() == Node.ELEMENT_NODE) {
									Element element_Settings = (Element) node_Settings;

									// Get Node - Setting
									NodeList nodeList_Setting = element_Settings
											.getElementsByTagName("Setting");

									for (int i32SettingIndex = 0; i32SettingIndex < nodeList_Setting
											.getLength(); i32SettingIndex++) {
										Node node_Setting = nodeList_Setting
												.item(i32SettingIndex);
										if (node_Setting.getNodeType() == Node.ELEMENT_NODE) {
											Element element_Setting = (Element) node_Setting;
											m_aryListGPXMLValues.clear();

											// Get Setting Name
											NodeList nodeList_SettingName = element_Setting
													.getElementsByTagName("Name");
											if (nodeList_SettingName
													.getLength() > 0) {
												strSettingName = ((Node) ((Element) nodeList_SettingName
														.item(0))
														.getChildNodes()
														.item(0))
														.getNodeValue();
											} else
												strSettingName = "";

											// Get Setting ID
											NodeList nodeList_SettingID = element_Setting
													.getElementsByTagName("ID");
											if (nodeList_SettingID.getLength() > 0) {
												strSettingID = ((Node) ((Element) nodeList_SettingID
														.item(0))
														.getChildNodes()
														.item(0))
														.getNodeValue();
											} else
												strSettingID = "";

											// Get Setting Type
											NodeList nodeList_SettingType = element_Setting
													.getElementsByTagName("Type");
											if (nodeList_SettingType
													.getLength() > 0) {
												strSettingType = ((Node) ((Element) nodeList_SettingType
														.item(0))
														.getChildNodes()
														.item(0))
														.getNodeValue();
											} else
												strSettingType = "";
											
											// Get Setting Reflash
											NodeList nodeList_SettingReflash = element_Setting
													.getElementsByTagName("Reflash");
											if (nodeList_SettingReflash
													.getLength() > 0) {
												strSettingReflash = ((Node) ((Element) nodeList_SettingReflash
														.item(0))
														.getChildNodes()
														.item(0))
														.getNodeValue();
											} else
												strSettingReflash = "";

											// Get Setting Default
											NodeList nodeList_SettingDefault = element_Setting
													.getElementsByTagName("Default");
											if (nodeList_SettingDefault
													.getLength() > 0) {
												strSettingDefault = ((Node) ((Element) nodeList_SettingDefault
														.item(0))
														.getChildNodes()
														.item(0))
														.getNodeValue();
											} else
												strSettingDefault = "";

											// Get Node - Values
											NodeList nodeList_Values = element_Setting
													.getElementsByTagName("Values");

											for (int i32ValuesIndex = 0; i32ValuesIndex < nodeList_Values
													.getLength(); i32ValuesIndex++) {
												Node node_Values = nodeList_Values
														.item(i32ValuesIndex);
												if (node_Values.getNodeType() == Node.ELEMENT_NODE) {
													Element element_Values = (Element) node_Values;

													// Get Node - Value
													NodeList nodeList_Value = element_Values
															.getElementsByTagName("Value");
													for (int i32ValueIndex = 0; i32ValueIndex < nodeList_Value
															.getLength(); i32ValueIndex++) {
														Node node_Value = nodeList_Value
																.item(i32ValueIndex);
														if (node_Value
																.getNodeType() == Node.ELEMENT_NODE) {
															Element element_Value = (Element) node_Value;

															// Get Value Name
															NodeList nodeList_ValueName = element_Value
																	.getElementsByTagName("Name");
															if (nodeList_ValueName
																	.getLength() > 0) {
																strValueName = ((Node) ((Element) nodeList_ValueName
																		.item(0))
																		.getChildNodes()
																		.item(0))
																		.getNodeValue();
															} else
																strValueName = "";

															// Get Value ID
															NodeList nodeList_ValueID = element_Value
																	.getElementsByTagName("ID");
															if (nodeList_ValueID
																	.getLength() > 0) {
																strValueID = ((Node) ((Element) nodeList_ValueID
																		.item(0))
																		.getChildNodes()
																		.item(0))
																		.getNodeValue();
															} else
																strValueID = "";

															m_GPXMLValue = new GPXMLValue(
																	strValueName,
																	strValueID,
																	(i32CategoryIndex * (0x01 << CategoryLevel))
																			+ (i32SettingIndex * (0x01 << SettingLevel))
																			+ (i32ValueIndex * (0x01 << ValueLevel)));
															m_aryListGPXMLValues
																	.add(m_GPXMLValue);
															m_GPXMLValue = null;
														}
													}
												}
											}

											m_GPXMLSetting = new GPXMLSetting(
													strSettingName,
													strSettingID,
													strSettingType,
													strSettingReflash,
													strSettingDefault,
													(i32CategoryIndex << CategoryLevel | i32SettingIndex << SettingLevel),
													m_aryListGPXMLValues);
											m_aryListGPXMLSettings
													.add(m_GPXMLSetting);
											m_GPXMLSetting = null;
										}
									}
								}
							}
							m_GPXMLCategory = new GPXMLCategory(
									strCategoryName,
									(i32CategoryIndex << CategoryLevel),
									m_aryListGPXMLSettings);
							m_aryListGPXMLCategroies.add(m_GPXMLCategory);
							m_GPXMLCategory = null;
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(GPTag, e.getMessage());
		}

		return m_aryListGPXMLCategroies;
	}
}

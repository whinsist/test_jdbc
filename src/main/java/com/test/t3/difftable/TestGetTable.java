package com.test.t3.difftable;

import com.kugua.util.JdbcUtils;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

public class TestGetTable {

	public static final List<String> excludList = Arrays.asList("cloud_tag", "cloud_tag_ref", "license", "open_api_key",
											"ops", "qrtz",
											"schema_version", "sequence",
											"sys_m_code", "sys_m_company", "sys_m_mail_template", "sys_m_module", "sys_m_module_permission", "sys_m_module_rule",
											"sys_m_org", "sys_m_project", "sys_m_role", "sys_m_user", "sys_t_action_log", "sys_t_attachment",
											"sys_t_log_record", "sys_t_msg",
											// 有外键删不了的暂时不删
											"cloud_exa_report", "cloud_market_place");


	public static void main(String[] args) throws Exception {
		String mainUrl = "jdbc:mysql://192.168.93.132:3306/rightcloud_autoops";
		String mainUsername = "root";
		String mainPw = "123456";

        List<String> tableList = JdbcUtils.getTables(mainUrl, mainUsername, mainPw);
        System.out.println(tableList.size());



        StringBuilder builder = new StringBuilder();
        for (String table : tableList) {

        	boolean isDelete = getIsDelete(table);
        	if (isDelete) {
				String sql = "drop table if exists "+table+";";
				Connection connection = JdbcUtils.getConnection(mainUrl, mainUsername, mainPw);
				PreparedStatement statement = connection.prepareStatement(sql);
				int i = statement.executeUpdate();
				System.out.println("i=========="+i);

				builder.append(sql + "\n");
			}
		}

		FileUtils.writeStringToFile(new File("e:/temp/deletetable.sql"), builder.toString());
	}

	private static boolean getIsDelete(String table) {
		for (String prefix : excludList) {
			if (table.startsWith(prefix)) {
				return false;
			}
		}
		return true;
	}
}

package com.test.t10.RoleModule;

import java.util.Arrays;
import java.util.List;

/**
 * @author Hong.Wu
 * @date: 13:54 2020/03/30
 */
public class GeneratorModuleSQL {

    private static List<String> deleteModulesIds = Arrays.asList();


    private static List<String> modulesIds = Arrays.asList("GA", "GA01", "GA02", "GA03",
                                                           "GC", "GC01", "GC02", "GC03", "GC04",
                                                           "GD", "GD01", "GD02", "GD03", "GD04", "GD05");


    private static String MODULE_DELETE_SQL = "DELETE FROM sys_m_role_module WHERE role_sid = 101 AND module_sid IN ";
    private static String MODULE_INSERT_SQL = "INSERT INTO sys_m_role_module (role_sid, module_sid) VALUES ";
    private static String DEFAULT_DELETE_SQL = "DELETE FROM sys_m_role_module_default WHERE role_sid = 101 AND module_sid IN ";
    private static String DEFAULT_INSERT_SQL = "INSERT INTO sys_m_role_module_default (role_sid, module_sid) VALUES ";
    private static String only_delete_modeule_sql = "DELETE FROM sys_m_role_module WHERE module_sid IN ";
    private static String only_delete_modeule_sql_default = "DELETE FROM sys_m_role_module_default WHERE module_sid IN ";






    public static void main(String[] args) {
        String sidInStr = getSidInStr(modulesIds);
        String valuesStr = getRoleIdMoudleSidValueStr(modulesIds);

        System.out.println("#权限and默认权限");
        System.out.println(MODULE_DELETE_SQL + sidInStr + ";");
        System.out.println(MODULE_INSERT_SQL + valuesStr + ";");
        System.out.println("");
        System.out.println(DEFAULT_DELETE_SQL + sidInStr + ";");
        System.out.println(DEFAULT_INSERT_SQL + valuesStr + ";");

        System.out.println("");
        System.out.println("#删除取消的权限and默认权限");
        System.out.println(only_delete_modeule_sql + getSidInStr(deleteModulesIds) + ";");
        System.out.println(only_delete_modeule_sql_default + getSidInStr(deleteModulesIds) + ";");
    }

    /**
     * 返回如：(101, 'MD0344'), (101, 'MD0345')
     */
    private static String getRoleIdMoudleSidValueStr(List<String> modulesIds) {
        StringBuffer valuesStr = new StringBuffer();
        for (int i = 0; i < modulesIds.size(); i++) {
            valuesStr.append("(101, '" + modulesIds.get(i) + "')");
            if (i < modulesIds.size() - 1) {
                valuesStr.append(", ");
            }
        }
        return valuesStr.toString();
    }

    /**
     * 返回如：('MD0344', 'MD0345')
     */
    private static String getSidInStr(List<String> modulesIds) {
        StringBuffer sidInStr = new StringBuffer("(");
        for (int i = 0; i < modulesIds.size(); i++) {
            sidInStr.append("'" + modulesIds.get(i) + "'");
            if (i < modulesIds.size() - 1) {
                sidInStr.append(", ");
            } else {
                sidInStr.append(")");
            }
        }
        return sidInStr.toString();
    }


}

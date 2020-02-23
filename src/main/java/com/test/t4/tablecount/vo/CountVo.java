package com.test.t4.tablecount.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Hong.Wu
 * @date: 14:30 2020/02/07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountVo implements Serializable {

    private String tableName;
    private Integer count;

    @Override
    public String toString() {
        return " {" + tableName + ':' +  count + '}';
    }
}

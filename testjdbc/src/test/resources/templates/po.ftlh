<#-- 
package me.ooi.demo.testjdbc;
 -->
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author jun.zhao
 * ${date}
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ${name} {
<#list fieldMap as key, value>

    /**
     * ${CommentUtils.getComment(key)}
    */
    @ExcelProperty("${CommentUtils.getComment(key)}")
    private ${TypeMapping.getMapping(value).getName()} ${EntityUtils.dbFieldToBeanField(key)};
</#list>

}

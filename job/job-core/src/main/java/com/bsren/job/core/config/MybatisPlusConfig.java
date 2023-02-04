package com.bsren.job.core.config;


import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public CustomizedSqlInjector customizedSqlInjector() {
        return new CustomizedSqlInjector();
    }

    class CustomizedSqlInjector extends DefaultSqlInjector {
        @Override
        public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
            List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
            methodList.add(new InsertBatchMethod());
            methodList.add(new UpdateBatchMethod());
            return methodList;
        }
    }
    public class InsertBatchMethod extends AbstractMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            final String sql = "<script>insert into %s %s values %s</script>";
            final String fieldSql = prepareFieldSql(tableInfo);
            final String valueSql = prepareValuesSql(tableInfo);
            final String sqlResult = String.format(sql, tableInfo.getTableName(), fieldSql, valueSql);
            //log.debug("sqlResult----->{}", sqlResult);
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
            return this.addInsertMappedStatement(mapperClass, modelClass, "insertBatch", sqlSource, new NoKeyGenerator(), null, null);
        }

        private String prepareFieldSql(TableInfo tableInfo) {
            StringBuilder fieldSql = new StringBuilder();
            fieldSql.append(tableInfo.getKeyColumn()).append(",");
            tableInfo.getFieldList().forEach(x -> fieldSql.append(x.getColumn()).append(","));
            fieldSql.delete(fieldSql.length() - 1, fieldSql.length());
            fieldSql.insert(0, "(");
            fieldSql.append(")");
            return fieldSql.toString();
        }

        private String prepareValuesSql(TableInfo tableInfo) {
            final StringBuilder valueSql = new StringBuilder();
            valueSql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"(\" separator=\"),(\" close=\")\">");
            valueSql.append("#{item.").append(tableInfo.getKeyProperty()).append("},");
            tableInfo.getFieldList().forEach(x -> valueSql.append("#{item.").append(x.getProperty()).append("},"));
            valueSql.delete(valueSql.length() - 1, valueSql.length());
            valueSql.append("</foreach>");
            return valueSql.toString();
        }
    }

    public class UpdateBatchMethod extends AbstractMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            String sql = "<script>\n<foreach collection=\"list\" item=\"item\" separator=\";\">\nupdate %s %s where %s=#{%s} %s\n</foreach>\n</script>";
            String additional = tableInfo.isWithVersion() ? tableInfo.getVersionFieldInfo().getVersionOli("item", "item.") : "" + tableInfo.getLogicDeleteSql(true, true);
            String setSql = sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, "item", "item.");
            String sqlResult = String.format(sql, tableInfo.getTableName(), setSql, tableInfo.getKeyColumn(), "item." + tableInfo.getKeyProperty(), additional);
            //log.debug("sqlResult----->{}", sqlResult);
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
            // 第三个参数必须和RootMapper的自定义方法名一致
            return this.addUpdateMappedStatement(mapperClass, modelClass, "updateBatch", sqlSource);
        }
    }
}

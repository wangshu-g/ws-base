package com.ws.annotation;

import com.ws.base.model.BaseModel;
import com.ws.enu.JoinCondition;
import com.ws.enu.JoinType;

import java.lang.annotation.*;

/**
 * 标记连表查询字段
 * JOIN
 *
 * @author GSF
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JOIN {

    /**
     * <p>
     * 目标join表,建议指定
     * 如果不指定且该字段类型是BaseModel的子类,该值则为该字段的类型
     * </p>
     *
     * @author wangshuhunyin
     **/
    Class<? extends BaseModel> leftTable() default BaseModel.class;

    /**
     * <p>关联属性</p>
     *
     * @author wangshuhunyin
     **/
    String leftJoinField() default "id";

    /**
     * <p>查询关联表的属性,默认目标join类的所有属性或自己指定</p>
     *
     * @author wangshuhunyin
     **/
    String[] leftSelectFields() default {"*"};

    /**
     * <p>
     * 一般在中间表查询时指定(建议要指定)
     * 如果不指定,默认是当前属性所在类
     * </p>
     *
     * @author wangshuhunyin
     **/
    Class<? extends BaseModel> rightTable() default BaseModel.class;

    /**
     * <p>关联属性</p>
     *
     * @author wangshuhunyin
     **/
    String rightJoinField() default "id";

    JoinType joinType() default JoinType.left;

    JoinCondition joinCondition() default JoinCondition.equal;

    /**
     * <p>用于连表alias</p>
     *
     * @author wangshuhunyin
     **/
    String infix() default "Model";

}

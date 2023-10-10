package com.cy.generated.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Haechi
 * @since 2023-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GameUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 筹码
     */
    private Integer point;

    public GameUser(String phone, String password) {
        this.phone = phone;
        this.password = password;
        this.point = 10000;
    }
}

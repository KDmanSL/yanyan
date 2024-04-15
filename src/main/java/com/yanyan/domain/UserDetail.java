package com.yanyan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 考研信息
 * @TableName yy_user_detail
 */
@TableName(value ="yy_user_detail")
@Data
public class UserDetail implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long userid;

    /**
     * 
     */
    private Long schoolid;

    /**
     * 
     */
    private Long majorid;

    /**
     * 成绩
     */
    private Double score;

    /**
     * 考研时间届(如2025)
     */
    private Integer session;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UserDetail other = (UserDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
            && (this.getSchoolid() == null ? other.getSchoolid() == null : this.getSchoolid().equals(other.getSchoolid()))
            && (this.getMajorid() == null ? other.getMajorid() == null : this.getMajorid().equals(other.getMajorid()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getSession() == null ? other.getSession() == null : this.getSession().equals(other.getSession()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
        result = prime * result + ((getSchoolid() == null) ? 0 : getSchoolid().hashCode());
        result = prime * result + ((getMajorid() == null) ? 0 : getMajorid().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getSession() == null) ? 0 : getSession().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userid=").append(userid);
        sb.append(", schoolid=").append(schoolid);
        sb.append(", majorid=").append(majorid);
        sb.append(", score=").append(score);
        sb.append(", session=").append(session);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
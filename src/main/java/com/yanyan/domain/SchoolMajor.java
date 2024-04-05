package com.yanyan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 院校专业
 * @TableName yy_school_major
 */
@TableName(value ="yy_school_major")
@Data
public class SchoolMajor implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long schoolid;

    /**
     * 专业
     */
    private Long majorid;

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
        SchoolMajor other = (SchoolMajor) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSchoolid() == null ? other.getSchoolid() == null : this.getSchoolid().equals(other.getSchoolid()))
            && (this.getMajorid() == null ? other.getMajorid() == null : this.getMajorid().equals(other.getMajorid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSchoolid() == null) ? 0 : getSchoolid().hashCode());
        result = prime * result + ((getMajorid() == null) ? 0 : getMajorid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", schoolid=").append(schoolid);
        sb.append(", majorid=").append(majorid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
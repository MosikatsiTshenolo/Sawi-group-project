package za.ac.spu.sawi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 30)
    private String roleName;

    // Default constructor required by JPA
    public Role() {
    }

    // Constructor
    public Role(String roleName) {
        this.roleName = roleName;
    }

    // Getter
    public Integer getRoleId() {
        return roleId;
    }

    // Setter
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    // Getter
    public String getRoleName() {
        return roleName;
    }

    // Setter
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
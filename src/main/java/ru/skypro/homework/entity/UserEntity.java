package ru.skypro.homework.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import ru.skypro.homework.dto.Role;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "айди")
    private Integer id;

    @Column(name = "почта", nullable = false, unique = true, length = 32)
    private String email;

    @Column(name = "пароль", nullable = false, length = 100)
    private String password;

    @Column(name = "имя", nullable = false, length = 16)
    private String firstName;

    @Column(name = "фамилия", nullable = false, length = 16)
    private String lastName;

    @Column(name = "телефон", nullable = false, length = 20)
    private String phone;

    @Column(name = "роль", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "картинка")
    private String image;

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public String getImage() {
        return image;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role setRole(Role role) {
        this.role = role;
        return role;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

package com.example.ReadBrew.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.ReadBrew.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int xp = 0; 

    @Column(nullable = false)
    private int level = 1; 


    @ManyToOne
    @JoinColumn(name = "avatar_id") 
    private Avatar profileAvatar;

    public String getAvatarUrl() {
    if (this.profileAvatar == null) {
        return "/avatars/default_user.png"; 
    }
    return "/avatars/" + this.profileAvatar.getFileName();
}

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER; 

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }



    // user's followers
    
    @ManyToMany
    @JoinTable(
        name = "user_followers", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    @EqualsAndHashCode.Exclude 
    @ToString.Exclude          
    private Set<User> followers = new HashSet<>();
   
    @ManyToMany(mappedBy = "followers")
    @EqualsAndHashCode.Exclude 
    @ToString.Exclude
    private Set<User> following = new HashSet<>();

    @Override
    public String getUsername() {
        return email; 
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public UserRole getRole() {
        return this.role;
    }
}
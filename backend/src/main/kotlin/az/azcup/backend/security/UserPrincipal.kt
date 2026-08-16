package az.azcup.backend.security

import az.azcup.backend.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(val user: User) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role?.name}"))

    override fun getPassword(): String = user.passwordHash

    override fun getUsername(): String = user.email
}

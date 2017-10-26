package dk.group11.auditsystem.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService : ISecurityService {

    override fun getId(): Long {
        return principal.id
    }

    val principal: UserData
        get() = SecurityContextHolder.getContext().authentication.principal as UserData
}
package dk.group11.auditsystem.security

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.ResponseDeserializable
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

data class UserData(var id: Long = 0, var username: String = "")

class AuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {


    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(HEADER_STRING)
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res)
            return
        }
        val authentication = getAuthentication(req)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, res)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(HEADER_STRING)
        val user = Jwts.parser()
                .setSigningKey(SECRET.toByteArray())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .body
                .subject

        val userData = ObjectMapper().readValue<UserData>(user, UserData::class.java)

        if (user != null) {
            return UsernamePasswordAuthenticationToken(userData, null, ArrayList())
        }
        return null
    }
}
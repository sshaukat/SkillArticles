package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.LoginReq
import ru.skillbranch.skillarticles.data.remote.req.RegistrationReq

object RootRepository {

    private val preferences = PrefManager
    private val network = NetworkManager.api

    fun isAuth(): LiveData<Boolean> = preferences.isAuthLive

    suspend fun login(login: String, pass: String) {
        val auth = network.login(LoginReq(login, pass))
        preferences.profile = auth.user
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }

    suspend fun handleRegister(name: String, login: String, password: String, dest: Int?) {
        val auth = network.register(RegistrationReq(name, login, password))
        preferences.profile = auth.user
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }
}
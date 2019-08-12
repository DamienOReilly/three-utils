package org.damienoreilly.threeutils.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.launch
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Error
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Success
import org.damienoreilly.threeutils.worker.My3Worker
import java.util.concurrent.TimeUnit

class My3SetupViewModel(
        private val preferenceStorage: PreferenceStorage,
        private val my3Repository: My3Repository,
        private val workManager: WorkManager
) : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val loginError = MutableLiveData<String>()
    val working = MutableLiveData<Boolean>()

    fun validateCredentials() {
        if (username.value != null && password.value != null) {
            working.postValue(true)
            viewModelScope.launch {
                try {
                    when (val login = my3Repository.login(username.value!!, password.value!!)) {
                        is Success -> saveCredentialsAndSetupWorker(username.value!!, password.value!!)
                        is Error -> loginError.postValue(login.error.toString())
                    }
                } catch (e: Exception) {
                    loginError.postValue("Error connecting to My3.")
                }
                working.postValue(false)
            }

        }

    }

    private fun saveCredentialsAndSetupWorker(username: String, password: String) {
        preferenceStorage.my3UserName = username
        preferenceStorage.my3Password = password

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val work = PeriodicWorkRequestBuilder<My3Worker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .build()

        workManager.enqueueUniquePeriodicWork("my3_usage_refresh",
                        ExistingPeriodicWorkPolicy.KEEP, work)
    }


}
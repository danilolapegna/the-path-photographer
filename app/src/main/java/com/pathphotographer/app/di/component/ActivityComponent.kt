package com.pathphotographer.app.di.component

import com.pathphotographer.app.di.module.ActivityModule
import com.pathphotographer.app.di.scope.ActivityScope
import com.pathphotographer.app.ui.activity.MainActivity
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
@ActivityScope
interface ActivityComponent {

    fun inject(activity: MainActivity)
}
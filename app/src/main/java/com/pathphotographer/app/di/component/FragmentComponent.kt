package com.pathphotographer.app.di.component

import com.pathphotographer.app.di.module.FragmentModule
import com.pathphotographer.app.di.scope.FragmentScope
import com.pathphotographer.app.ui.fragment.MainFragment
import dagger.Subcomponent

@Subcomponent(modules = [FragmentModule::class])
@FragmentScope
interface FragmentComponent {

    fun inject(fragment: MainFragment)
}
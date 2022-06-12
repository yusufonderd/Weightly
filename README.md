# Weightly(Still in development)
Weightly is simple weight tracker app. It stores data from the scale to monitor and analyze weight loss.

## Screenshots

<p align="center">
<img src="https://user-images.githubusercontent.com/13941871/173245722-a91d2fb1-59d2-4138-bcb2-4c456cfcbb04.png" width="24%" />  
<img src="https://user-images.githubusercontent.com/13941871/173245571-566d1fd3-cfe6-4677-8f4d-5d4b253a5859.png" width="24%" />  
<img src="https://user-images.githubusercontent.com/13941871/173245648-ae74f3ee-63db-4793-bbd6-e6d5fd47c0e2.png" width="24%" />  

</p>


## Architecture
This app implements MVVM architecture. App consist of different fragments and 1 root activity. Activity holds a container layout in order to manage fragments which will be controlled by navigation component. Here is the package structure:

#### Core
It is the package that contains all the common and base classes used within the application. 
Extensions, deciders, utils and base classes are included in this package.

#### Data
Data package should include response models, data source and api methods. It shouldn't know any logic.

#### UI 
UI like a feature. It contains Fragments, view models and feature related classes like a domains, mappers and ui models.
Make sure that all classes here are specific to the this feature. If it is a class that is also used in other features, it should be moved to the common package.

#### Di
This package may actually be inside the common module. But I prefer to carry outside of core package to be more visible. 

#### Ui-Component
In large projects, we need to use a view component in more than one place. So i moved common view components under ui-component package.

## Tech Stack
* [Kotlin](https://kotlinlang.org/) , [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) , [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
* [Dagger-Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Dependency Injection
* [MVVM Architecture](https://developer.android.com/jetpack/guide) - Modern, maintainable, and Google suggested app architecture
* [Room](https://developer.android.com/jetpack/androidx/releases/room) - For offline storage
* [Navigation Component](https://developer.android.com/guide/navigation) - Single activity multiple fragments approach
* [View Binding](https://developer.android.com/topic/libraries/view-binding) 
* [StateLayout](https://github.com/yusufonderd/StateLayout) - For managing view states


## TODOs and Improvements
- UI test.
- Better Design
- Unit tests
- Implementation of static code analysis tool(ktlint or detekt)
- Styling definitions for textviews and buttons etc.

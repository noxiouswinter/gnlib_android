gnlib_android
=============

GNLib is an Android utilities library project aimed at identifying and solving some of the common tasks/issues every android application developer has to tackle frequently. The first version of the library features the GNLauncher and the GNStateManager. 

GNLauncher makes sending objects/data to an Activity from another Activity etc as easy as calling a function in tha Activity with the required data as parameters. It introduces type safety and removes all the hastles of having to serialize, attaching to the intent using string keys and undoing the same at the other end.

GNStateManager makes saving and retrieving the state/fields of an Activity etc as easy as just annotating the required fields and adding a call to the GNStateManager in the OnCreate and OnPause methods. Custom objects are supported and their fields can be selectively saved/retrieved too. 

The master branch has the gnlib-android module and the sample_application module contains a working example of these features. 

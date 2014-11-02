gnlib_android
=============

GNLib is an Android utilities library project aimed at identifying and solving some of the common tasks/issues every android application developer has to tackle frequently. The first version of the library features the GNLauncher and the GNStateManager.

<b>GNLauncher</b> makes sending objects/data to an Activity from another Activity etc as easy as calling a function in tha Activity with the required data as parameters. It introduces type safety and removes all the hastles of having to serialize, attaching to the intent using string keys and undoing the same at the other end.

<b>GNStateManager</b> makes saving and retrieving the state/fields of an Activity etc as easy as just annotating the required fields and adding a call to the GNStateManager in the OnCreate and OnPause methods. Custom objects are supported and their fields can be selectively saved/retrieved too. 

The <b>master</b> branch has the gnlib-android module with the source code with tests and the <b>sample_application</b> branch contains a working example of these features and using the <b>gnlib-android jar</b>. Please see the <b><a href = 'https://github.com/noxiouswinter/gnlib_android/releases'>releases page</a></b> for the jars.
Please note that <b>GNLib has a dependency on the Gson 1.7.2 library</b>. <a href ='https://github.com/noxiouswinter/gnlib_android/wiki'>Please see the WIKI</a> for detailed instructions on setup and usage.

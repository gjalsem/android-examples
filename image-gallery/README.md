# Android image gallery example

A test project I wrote to get acquainted with the ViewModel and LiveData libraries from the Android Architecture
Components.

It shows images from either https://www.themoviedb.org/ or https://www.flickr.com/, in a grid and full screen on click.

The API keys used to fetch images are not provided in this repository. To try out the app, fill in your own API key in
TheMovieDbClient.java or FlickrClient.java.

The app uses TheMovieDbClient by default. To use FlickrClient, MainViewModelFactory.java needs to be edited, since there
is currently no UI option to switch.

# NextTech-SpanishReview
Spanish Review Sheet for Ms. King for the sophomore year SMCS Software Engineering Project.

This code uses a lot of examples from Google App Engine sources.

This application uses Google App Engine Java Standard Environment. Learn about how this works before trying to read this code by visiting [their website](https://cloud.google.com/appengine/docs/java/).

A lot of the files in here (such as the .yaml and .xml files) are used by App Engine. This is all described on [their website](https://cloud.google.com/appengine/docs/java/).

To deploy yourself, you will need to have a billing account set up in your Cloud Platform Console, and then you will have to configure your GCloud SDK to use that account.

The current version of this project is running at [phs-spanishreview.appspot.com](phs-spanishreview.appspot.com).

## Running locally 
This example uses the
[Maven gcloud plugin](https://cloud.google.com/appengine/docs/java/managed-vms/maven).
To run this sample locally:

    $ mvn appengine:devserver

    This will run at `localhost:8080`.

## Deploying

    $ mvn appengine:update

    This will push to `YOUR_PROJECT_ID`.appspot.com.


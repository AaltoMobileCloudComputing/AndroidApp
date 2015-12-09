# MCC 16 App

This is project done for Aalto University course [Mobile Cloud Computing T-110.5121](to.fi/course/view.php?id=8779) 

# How to install

App can be installed by transferring mcc-app-release.apk to Android phone and installing it.
User has to allow installing from unknown sources.


# How to use app

## Login

First thing that user needs to do is log in.
User must first create account in web UI.
Note! You must first deploy server/backend, please check [server](https://github.com/AaltoMobileCloudComputing/Server)

### Note

Application backend is deployed to public cloud. In this build this public cloud is used.

## Add event

Click the button with plus icon to create new event. Events (and any edits) are added directly to the backend. Offline usage of the app is not supported.

## Edit event

Click one of the events in the main list to edit that particular event.

## Delete event

Click the trash can button in event editing view to delete the event.

## Export event to device calendars

User can export any event from service into devices calendars.
Event exporting can be started by long clicking/pushing event card from list of events.
This opens list of users calendars in device calendars.
Click wanted calendar and event is exported into selected calendar.

## Import event to service

User can import events from device calendars.
To do this user must long click floating button in event list.
This opens list of all upcoming events.
By clicking event this event will be added to service.

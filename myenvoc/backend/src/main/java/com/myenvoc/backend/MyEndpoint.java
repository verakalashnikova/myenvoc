/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.myenvoc.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.users.User;
import javax.inject.Named;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import static com.googlecode.objectify.ObjectifyService.ofy;
/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",

        clientIds = {Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID},
        audiences = {Ids.ANDROID_AUDIENCE}
)
public class MyEndpoint {
    static {
        ofy().factory().register(Car.class);
    }
    @Entity
    public class Car {
        @Id Long id;
        @Index String license;
        int color;

        public Car(String license, int color) {
            this.license = license;
            this.color = color;
        }
    }
    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name, User user) {
        Car porsche = new Car("2FAST", 2);
        ofy().save().entity(porsche).now();

        MyBean response = new MyBean();
        response.setData("Hi, " + name + getUserName(user) + ", id: " + porsche.id);

        return response;
    }

    @ApiMethod(name = "sayHi2")
    public MyBean sayHi2(@Named("name") String name, User user) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name + getUserName(user));

        return response;
    }

    private String getUserName(User user) {
        if (user == null) {
            return ". Not authenticated";
        }
        return ". Your account is: " + user.getEmail() + ", " + user.getUserId();
    }

}

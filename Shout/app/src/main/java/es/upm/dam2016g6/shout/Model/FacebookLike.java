package es.upm.dam2016g6.shout.Model;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

/**
 * Created by pthalamy on 22/11/16.
 */

public class FacebookLike {
    private String pictureUrl;
    private String name;
    private String id;

    public FacebookLike(String name, String id) {
        this.name = name;
        this.id = id;

        fetchPictureUrl();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacebookLike that = (FacebookLike) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void fetchPictureUrl() {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                this.id,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Handle result
                        try
                        {
                            FacebookLike.this.setPictureUrl(response.getJSONObject()
                                    .getJSONObject("picture")
                                    .getJSONObject("data")
                                    .getString("url")
                            );
                        }
                        catch ( Throwable t )
                        {
                            System.err.println( t );
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture");
        request.setParameters(parameters);

        request.executeAsync();
    }
}

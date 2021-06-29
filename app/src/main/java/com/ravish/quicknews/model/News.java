package com.ravish.quicknews.model;

public class News {

    /** This will store the Title of the latest news.*/
    private String mTitle;

    /** This will store the information about who published this news.*/
    private String mName;

    /** This will store the information about the image associated with that news.*/
    private String mImageUrl;

    /** This will store the short description about that news.*/
    private String mDescription;

    /** This wills store when the news is published.*/
    private String mDate;

    /** This will give us the Url of the Particular news.*/
    private String mUrl;

    /**
     * Construct a news object.
     * @param title is the title of the news.
     * @param name is the name of the publisher.
     * @param imageUrl is the image url associated with the news.
     * @param description is the the short description of the news.
     * @param date will show the time of the news published.
     * @param url will give the Url of the news.
     */
    public News(String title, String name, String imageUrl, String description, String date, String url){
        mTitle = title;
        mName = name;
        mImageUrl = imageUrl;
        mDescription = description;
        mDate = date;
        mUrl = url;
    }

    /**
     * Returns the title of the news.
     */
    public String getTitle(){
        return mTitle;
    }

    /**
     * Returns the name of the publisher.
     */
    public String getName(){
        return mName;
    }

    /**
     * Returns the image url associated with the news.
     */
    public String getImageUrl(){
        return mImageUrl;
    }

    /**
     * Returns the short description of the news.
     */
    public String getDescription(){
        return  mDescription;
    }

    /**
     * Returns the date.
     */
    public String getDate(){
        return mDate;
    }

    /**
     * Returns the Url of the news.
     */
    public String getUrl() {
        return mUrl;
    }
}

package de.dala.simplenews.common;

import java.io.Serializable;

public class Entry implements Comparable<Entry>, Serializable {
    private Long id;
    private Long feedId;
    private Long categoryId;
    private String title;
    private String description;
    private Long date;
    private String srcName;
    private String link;
    private String imageLink;
    private String shortenedLink;

    private boolean visible = true;
    private boolean expanded = false;
    private Long favoriteDate;
    private Long visitedDate;


    public Entry() {
    }

    public Entry(Long id, Long feedId, Long categoryId, String title, String description, Long date, String srcName, String link, String imageLink, Long visitedDate, Long favoriteDate, boolean isExpanded) {
        this.id = id;
        this.feedId = feedId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.srcName = srcName;
        this.link = link;
        this.imageLink = imageLink;
        this.favoriteDate = favoriteDate;
        this.visitedDate = visitedDate;
        this.expanded = isExpanded;
    }
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeedId() {
        return feedId;
    }

    public void setFeedId(Long feedId) {
        this.feedId = feedId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getShortenedLink() {
        return shortenedLink;
    }

    public void setShortenedLink(String shortenedLink) {
        this.shortenedLink = shortenedLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int compareTo(Entry another) {
        return another.getDate().compareTo(getDate());
    }

    @Override
    public String toString() {
        if (shortenedLink != null) {
            return String.format("%s - %s", title, shortenedLink);
        }
        return String.format("%s - %s", title, link);
    }

    public Long getVisitedDate() {
        return visitedDate;
    }

    public void setVisitedDate(Long visitedDate) {
        this.visitedDate = visitedDate;
    }

    public Long getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(Long favoriteDate) {
        this.favoriteDate = favoriteDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (date != null ? !date.equals(entry.date) : entry.date != null) return false;
        if (id != null ? !id.equals(entry.id) : entry.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}

package course;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class BlogPostDAO {
    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {

        // XXX HW 3.2,  Work Here

        Document post = null;
        FindIterable<Document> itr = postsCollection.find(eq("permalink",permalink));
        post = itr.first();


        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        // XXX HW 3.2,  Work Here
        // Return a list of DBObjects, each one a post from the posts collection

        List<Document> posts = new ArrayList<>();
        //postsCollection.find().limit(limit).forEach((Consumer<? super Document>) posts::add);
        FindIterable<Document> cursor = postsCollection.find()
                .sort(new BasicDBObject().append("date", -1)).limit(limit);
        //posts.sort((doc1, doc2) -> doc2.getDate("date").compareTo(doc1.getDate("date")));
        posts = new LinkedList<>();
        for (Document value : cursor) {
            posts.add(value);
        }
        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();


        // XXX HW 3.2, Work Here
        // Remember that a valid post has the following keys:
        // author, body, permalink, tags, comments, date, title
        //
        // A few hints:
        // - Don't forget to create an empty list of comments
        // - for the value of the date key, today's datetime is fine.
        // - tags are already in list form that implements suitable interface.
        // - we created the permalink for you above.

        // Build the post object and insert it
        List<Object> comments = new ArrayList<>();
        Document post = new Document();
        post.put("author",username);
        post.put("title", title);
        post.put("body",body);
        post.put("permalink",permalink);
        post.put("tags",tags);
        post.put("date",new Date());

        post.put("comments",comments);

        postsCollection.insertOne(post);
        return permalink;
    }




    // White space to protect the innocent








    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // XXX HW 3.3, Work Here
        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments
//        Document comment = new Document().append("body",body).append("author",name);
//        if(email != null) {
//            comment.append("email",email);
//        }
//        Document updatedDoc = findByPermalink(permalink);
//        updatedDoc.append("comments",comment);
//        System.out.println(updatedDoc);
//        postsCollection.updateOne(eq("permalink",permalink), new Document("$set",
//                updatedDoc));
        BasicDBObject comment = new BasicDBObject("author",name).append("body", body);
        if(email != null && !email.equals("")){
            comment.append("email",email);
        }
        postsCollection.updateOne(new BasicDBObject("permalink",permalink),new BasicDBObject("$push",new BasicDBObject("comments",comment)));
    }
}

package youtrack;

import org.apache.commons.codec.Charsets;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Created by egor.malyshev on 31.03.2014.
 */
final class AddComment extends AddCommand<Issue, IssueComment> {
    AddComment(@NotNull Issue owner) {
        super(owner);
    }

    @Override
    HttpRequestBase createMethod() {
        final HttpPost result = new HttpPost(owner.getYouTrack().getHostAddress() + "issue/" + getOwner().getId() + "/execute");
        result.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("comment", item.getText())), Charsets.toCharset("UTF-8")));
        return result;
    }
}
package youtrack;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.FileEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by egor.malyshev on 03.04.2014.
 */
final class AddAttachment extends AddCommand<Issue, IssueAttachment> {
    AddAttachment(Issue owner) {
        super(owner);
    }

    @Override
    HttpRequestBase createMethod() {
        final HttpPost postMethod = new HttpPost(owner.getYouTrack().getHostAddress() + "issue/" + getOwner().getId() + "/attachment");
        postMethod.setEntity(new FileEntity(new File(getItem().getUrl())));
        return postMethod;
    }
}
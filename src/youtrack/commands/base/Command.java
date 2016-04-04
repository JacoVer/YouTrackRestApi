package youtrack.commands.base;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.commons.httpclient.HttpMethodBase;
import youtrack.*;
import youtrack.Error;
import youtrack.exceptions.AuthenticationErrorException;
import youtrack.exceptions.CommandExecutionException;
import youtrack.issue.fields.*;
import youtrack.issue.fields.values.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egor.malyshev on 31.03.2014.
 */
public abstract class Command<O extends BaseItem, R> {
    protected final O owner;
    protected HttpMethodBase method;
    protected Map<String, String> parameters = new HashMap<String, String>();

    public Command(final @NotNull O owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Command " + this.getClass().getSimpleName() + "{" +
                "owner=" + owner +
                '}';
    }

    @NotNull
    public O getOwner() {
        return owner;
    }

    public boolean usesAuthorization() {
        return true;
    }

    @Nullable
    public abstract R getResult() throws CommandExecutionException, AuthenticationErrorException;

    /**
     * Helper method to deserealize XML to objects. Used to interpret XML response received from YouTrack.
     *
     * @param xmlString Raw XML code.
     * @return Instance of an object.
     */
    protected Object objectFromXml(final @NotNull String xmlString) throws Exception {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        Object result;
        final XMLStreamReader streamReader = new HackedReader(xmlInputFactory.createXMLStreamReader(new StringReader(xmlString)));
        final JAXBContext jaxbContext = JAXBContext.newInstance(AttachmentField.class,
                BaseIssueField.class,
                CustomField.class,
                CustomFieldValue.class,
                IssueField.class,
                IssueListField.class,
                LinkField.class,
                MultiUserField.class,
                SingleField.class, AttachmentFieldValue.class,
                BaseIssueFieldValue.class,
                IssueFieldValue.class,
                LinkFieldValue.class,
                MultiUserFieldValue.class, AttachmentList.class,
                CommentList.class,
                Issue.class,
                IssueAttachment.class,
                IssueComment.class,
                IssueCompactList.class,
                IssueLink.class,
                IssueProjectList.class,
                IssueTag.class,
                BaseItemList.class,
                SprintField.class,
                LinkList.class,
                ProjectList.class,
                TagList.class,
                Error.class);
        final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        result = jaxbUnmarshaller.unmarshal(streamReader);
        if (result instanceof Error) {
            Error error = (Error) result;
            error.setCode(method.getStatusLine().getStatusCode());
            throw new CommandExecutionException(this, error);
        }
        return result;
    }

    public HttpMethodBase getMethod() {
        return method;
    }

    public abstract void createCommandMethod() throws Exception;

    public void addParameter(final @NotNull String name, final @Nullable String value) {
        parameters.put(name, value);
    }

    public void removeParameter(final @NotNull String name) {
        if (parameters.containsKey(name)) parameters.remove(name);
    }

    /**
     * Class to work around the JAXB name handling.
     * Forces upper case on the first letter of xsi:type attribute.
     */
    private class HackedReader extends StreamReaderDelegate {
        public HackedReader(XMLStreamReader xmlStreamReader) {
            super(xmlStreamReader);
        }

        private final List<String> allowedNames = Arrays.asList("AttachmentField",
                "baseIssueField",
                "customField",
                "customFieldValue",
                "issueField",
                "issueListField",
                "linkField",
                "multiUserField",
                "singleField",
                "sprintField"
        );

        @Override
        public String getAttributeValue(int index) {
            final String attributeValue = super.getAttributeValue(index);
            if (!"type".equals(getAttributeLocalName(index))) return super.getAttributeValue(index);
            final String valueToReturn = attributeValue.substring(0, 1).toLowerCase() + attributeValue.substring(1);
            return valueToReturn.contains("Field") ? allowedNames.contains(valueToReturn) ? valueToReturn : "singleField" : valueToReturn;
        }
    }
}
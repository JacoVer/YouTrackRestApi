package youtrack;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import youtrack.commands.Command;
import youtrack.commands.GetProjects;
import youtrack.commands.Login;
import youtrack.commands.results.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Egor.Malyshev on 18.12.13.
 */
public class YouTrack {

	private final static Map<String, YouTrack> INSTANCES = new HashMap<String, YouTrack>();
	private final String hostAddress;

	private String authorization;

	private YouTrack(String hostAddress) {

		this.hostAddress = hostAddress;
	}

	/**
	 * Gets a @link YouTrack instance associated with given URL.
	 * <p/>
	 * URL must correspond to the base REST API URL of YouTrack server you're connecting to.
	 */

	public static YouTrack getInstance(String hostAddress) {

		if (!INSTANCES.containsKey(hostAddress)) INSTANCES.put(hostAddress, new YouTrack(hostAddress));

		return INSTANCES.get(hostAddress);
	}

	/**
	 * Executes a YouTrack command described by an object that extends @link Command class.
	 *
	 * @return instance of @link Result containing command execution results.
	 */

	Result execute(Command command) {

		Result result = null;

		try {

			HttpClient httpClient = new HttpClient();

			HttpMethodBase method = command.commandMethod(hostAddress);

			if (command.usesAuthorization()) {
				method.addRequestHeader("Cookie", authorization);
			}

			httpClient.executeMethod(method);

			result = new Result(command.getResult(), method.getStatusCode());

			method.releaseConnection();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Retrieve a list of projects from current connection.
	 *
	 * @return list of @link Project instances or null if there was an error.
	 */

	public List<Project> listProjects() {

		Result result = execute(new GetProjects());

		ProjectList projectList = (ProjectList) result.getData();

		if (projectList != null) {

			projectList.setYouTrack(this);
			return projectList.getProjectList();

		} else return null;

	}

	/**
	 * Tries to authorize current connection and returns true if successful, false otherwise.
	 * <p/>
	 * Retrieved token is stored for later use with all commands that need authentication.
	 */

	public boolean doLogin(String userName, String password) {

		Result result = execute(new Login(userName, password));

		if (result.success()) {

			authorization = (String) result.getData();

			return true;

		} else return false;

	}

	/**
	 * Retrieves a single project by id.
	 *
	 * @return @link Project instance or null if there was an error.
	 */

	public Project project(String id) {

		Result result = execute(new GetProjects());

		ProjectList projectList = (ProjectList) result.getData();

		if (projectList != null) {


			Project project = projectList.getProject(id);

			if (project != null) {
				project.setYouTrack(this);
				return project;
			}

		}

		return null;

	}

}
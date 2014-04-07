package youtrack;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egor.malyshev on 01.04.2014.
 */
@XmlRootElement(name = "projects")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectList {
	@XmlElement(name = "project")
	private List<Project> projectList;
	/*
	This is used to work around the issue with JAXB not being able to unmarshal a Map.
	 */
	@XmlTransient
	private Map<String, Project> projects;

	ProjectList() {

	}

	@Override
	public String toString() {
		return "ProjectList{" +
				"projectList=" + projectList +
				'}';
	}

	@SuppressWarnings("UnusedDeclaration")
	private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		projects = new HashMap<String, Project>();
		for (Project project : projectList) {
			projects.put(project.getId(), project);
		}
	}

	List<Project> getProjectList() {
		return new ArrayList<Project>(projects.values());
	}

	Project getProject(String id) {
		return (projects.containsKey(id)) ? projects.get(id) : null;
	}

	void setYouTrack(YouTrack youTrack) {
		for (Project project : projects.values()) {
			project.setYouTrack(youTrack);
		}
	}
}
package cs414.a1.rbetten;

import java.util.*;

public class Company {
	
	private String name;
	
	private HashSet<Worker> assignedWorkers = new HashSet<Worker>();
	private HashSet<Worker> availablePool = new HashSet<Worker>();
	private HashSet<Project> projectsPool = new HashSet<Project>();
	
	
	public Company(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public HashSet<Worker> getAvailableWorkers()
	{
		return availablePool;
	}
	
	public HashSet<Worker> getAssignedWorkers()
	{
		return assignedWorkers;
	}
	
	public HashSet<Worker> getUnassignedWorkers()
	{
		HashSet<Worker> unassignedWorkers = new HashSet<Worker>();
		
		// Checks through availablePool for workers that are not assigned to any projects then returns set
		for (Worker workerPool : availablePool) 
		{
			if( !assignedWorkers.contains(workerPool) )
			{
				unassignedWorkers.add(workerPool);
			}
		}
		
		return unassignedWorkers;
	}
	
	@Override
	public boolean equals(Object rightHandObject)
	{
		if(rightHandObject instanceof Company)
		{
			return name.equals(((Company)rightHandObject).getName());
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		// Returns "Company Name:Number of workers:Number of Projects"
		return name + ":" + availablePool.size() + ":" + projectsPool.size();
	}
	
	public void addToAvailableWorkerPool(Worker worker)
	{
		if(worker == null)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			// Check to see if worker is already in availablePool
			if( !availablePool.contains(worker) )
			{
				availablePool.add(worker);
			}
		}
	}
	
	public void assign(Project project, Worker worker)
	{
		if (project == null || worker == null)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			// CHecks to see if worker is helpful to project and will not be overloaded by being added
			// Is in the availablePool (Employee) and is not already added to project
			if ( project.isHelpful(worker) && !worker.willOverload(project) && !worker.getProjectsAssignedTo().contains(project) && availablePool.contains(worker) )
			{
				// Ensure project is not already running or finished
				if ( project.getStatus() != ProjectStatus.ACTIVE && project.getStatus() != ProjectStatus.FINISHED )
				{
					assignedWorkers.add(worker);
					worker.assignProject(project);
					project.assignWorker(worker);
				}
			}
		}
	}
	
	public void unassign(Project project, Worker worker)
	{
		if (project == null || worker == null)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			// Check to see if worker is on project
			if(assignedWorkers.contains(worker) && project.getWorkers().contains(worker))
			{
				// If so then remove worker from project and project from worker
				worker.unassignProject(project);
				project.unassignWorker(worker);
				
				// Check to see if project has any missing qualifications by removing worker
				if ( project.missingQualifications().size() > 0 )
				{
					project.setStatus(ProjectStatus.SUSPENDED);
				}
				if ( worker.getProjectsAssignedTo().size() == 0 )
				{
					assignedWorkers.remove(worker);
				}
			}
		}
	}
	
	public void unassignAll(Worker worker)
	{
		if (worker == null)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			// Removing worker from Company assignedWorkers
			assignedWorkers.remove(worker);
			
			// Removing worker from all projects worker is assigned to
			for(Project project : worker.getProjectsAssignedTo())
			{
				project.unassignWorker(worker);
				
				// If removing player means a project is missing qualifications it is suspended
				if( project.missingQualifications().isEmpty() )
				{
					project.setStatus(ProjectStatus.SUSPENDED);
				}
			}
			// Worker is no longer so clearing all projects
			worker.getProjectsAssignedTo().clear();
		}
	}
	
	public void start(Project project)
	{
		if (project == null)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			// Project can only be started if it is planned or suspended
			if ( project.getStatus() == ProjectStatus.PLANNED || project.getStatus() == ProjectStatus.SUSPENDED )
			{
				// Project must have no missingQualifications
				if ( project.missingQualifications().isEmpty() )
				{
					project.setStatus(ProjectStatus.ACTIVE);
				}
			}
		}
	}
	
	public void finish(Project project)
	{
		if (project == null)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			// Projects can only finish if they are set to active
			if ( project.getStatus() == ProjectStatus.ACTIVE )
			{
				project.setStatus(ProjectStatus.FINISHED);
				
				// All Workers are removed from project
				for ( Worker workersAssignedToProject : project.getWorkers() )
				{
					unassign(project, workersAssignedToProject);
				}
			}
		}
	}
	
	public Project createProject(String _name, HashSet<Qualification> qualSet, ProjectSize size)
	{
		Project proj = null;
		
		// Avoid NullPointerException
		if ( _name == null || qualSet == null || size == null )
		{
			throw new IllegalArgumentException();
		}
		// Enforce requirement that 1 to 1...* for project to qualifications
		else if ( qualSet.size() == 0 )
		{
			throw new IllegalArgumentException();
		}
		else
		{
			// Project is created and set to Planned
			proj = new Project(_name, size, ProjectStatus.PLANNED);
			projectsPool.add(proj);
			// Adds qualifications to project
			addQualificationsToProject(qualSet, proj);
		}

		return proj;
	}
	
	private void addQualificationsToProject(HashSet<Qualification> qualSet, Project proj)
	{
		for (Qualification qual : qualSet)
		{
			proj.addQualification(qual);
		}
	}
}

























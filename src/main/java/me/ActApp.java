package me;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//localhost:8080/go1
@RestController
@SpringBootApplication
public class ActApp {
    @Autowired
    public ActApp(RuntimeService runtimeService, TaskService taskService, HistoryService historyService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
    }

    private String procId;

    public static void main(String[] args) {
        SpringApplication.run(ActApp.class, args);
    }

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    @GetMapping("/go1")
    public void startProcess() {
        this.procId = runtimeService.startProcessInstanceByKey("financialReport").getId();

    }

    @GetMapping("/go2")
    public void claimTaskOfFirstGroup() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for accountancy group: " + task.getName());

            // claim it
            taskService.claim(task.getId(), "fozzie");
        }
    }


    @GetMapping("/go3")
    public void completeTaskOfFirstGroup() {
        //?????
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
        for (Task task : tasks) {
            System.out.println("Task for fozzie: " + task.getName());

            // Complete the task
            taskService.complete(task.getId());
        }

        System.out.println("Number of tasks for fozzie: "
                + taskService.createTaskQuery().taskAssignee("fozzie").count());

    }

    @GetMapping("/go4")
    public void claimTaskOfSecondGroup() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for management group: " + task.getName());
            taskService.claim(task.getId(), "kermit");
        }
    }

    @GetMapping("/go5")
    public void completeTaskOfSecondGroup() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            taskService.complete(task.getId());
        }
    }
    @GetMapping("/history")
    public void showHistory() {
    HistoricProcessInstance historicProcessInstance =
            historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
    System.out.println("Process instance end time: "+historicProcessInstance.getEndTime());
}
}

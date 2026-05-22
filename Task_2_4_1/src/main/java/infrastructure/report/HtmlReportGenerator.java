package infrastructure.report;

import core.model.StudentResult;
import core.model.TaskResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HtmlReportGenerator {

    public void generate(List<StudentResult> results, Path outputPath) throws IOException {
        if (results.isEmpty()) {
            Files.writeString(outputPath, "<html><body><h1>Нет данных для отчета</h1></body></html>");
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <title>Ведомость успеваемости ООП</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, sans-serif; margin: 40px; background-color: #f8f9fa; color: #333; }
                        h1 { text-align: center; color: #2c3e50; margin-bottom: 30px; }
                        h2 { color: #007bff; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-top: 50px; }
                        h3 { background: #e9ecef; padding: 10px; border-radius: 6px; color: #495057; margin-top: 30px; }
                        .semester-card { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); margin-bottom: 40px; }
                        table { border-collapse: collapse; width: 100%; margin: 20px 0; font-size: 0.9em; }
                        th, td { padding: 12px; border: 1px solid #dee2e6; text-align: center; }
                        th { background-color: #007bff; color: white; font-weight: 600; }
                        td:first-child { text-align: left; font-weight: bold; width: 250px; }
                        .ok { color: #28a745; font-weight: bold; }
                        .fail { color: #dc3545; font-weight: bold; }
                        .warning { color: #fd7e14; font-weight: bold; }
                        .activity-bar-bg { background: #e9ecef; border-radius: 10px; width: 80px; height: 8px; margin: 5px auto; }
                        .activity-bar-fill { height: 100%; border-radius: 10px; }
                        .summary-row { background-color: #f1f3f5; font-weight: bold; }
                        .grade-cell { text-transform: uppercase; letter-spacing: 1px; font-size: 0.85em; }
                    </style>
                </head>
                <body>
                    <h1>Сводная ведомость ООП</h1>
                """);

        renderSemesterBlock(html, results, 1, "Семестр 1");
        renderSemesterBlock(html, results, 2, "Семестр 2");

        html.append("""
                </body>
                </html>
                """);

        Files.writeString(outputPath, html.toString());
    }

    private void renderSemesterBlock(StringBuilder html, List<StudentResult> results, int part, String title) {
        boolean isPart1 = (part == 1);
        Set<String> taskIds = new HashSet<>();
        for (StudentResult res : results) {
            taskIds.addAll(isPart1 ? res.part1Results().keySet() : res.part2Results().keySet());
        }
        List<String> sortedTasks = taskIds.stream().sorted(Comparator.comparing(this::getTaskSortingKey)).toList();

        if (sortedTasks.isEmpty()) return;

        html.append("<div class='semester-card'>");
        html.append("<h2>").append(title).append("</h2>");

        html.append("<h3>Результаты тестирования</h3>");
        for (String tid : sortedTasks) {
            html.append("<p style='margin-top:20px;'><strong>Задание: ").append(tid).append("</strong></p>");
            html.append("<table><thead><tr><th>Студент</th><th>Build</th><th>Style</th><th>Tests</th><th>Coverage</th><th>Score</th></tr></thead><tbody>");
            for (StudentResult res : results) {
                TaskResult tr = (isPart1 ? res.part1Results() : res.part2Results()).getOrDefault(tid, TaskResult.failed());
                html.append("<tr><td>").append(res.student().fullName()).append("</td>");
                html.append("<td>").append(tr.compileSuccess() ? "<span class='ok'>OK</span>" : "<span class='fail'>FAIL</span>").append("</td>");
                html.append("<td>").append(tr.styleSuccess() ? "<span class='ok'>PASS</span>" : "<span class='warning'>WARN</span>").append("</td>");
                html.append("<td>").append(tr.tests().passed()).append("/").append(tr.tests().total()).append("</td>");
                html.append("<td>").append(String.format("%.1f%%", tr.coverage())).append("</td>");
                html.append("<td><strong>").append(tr.score()).append("</strong></td></tr>");
            }
            html.append("</tbody></table>");
        }

        html.append("<h3>Итог семестра</h3>");
        html.append("<table><thead><tr><th>Студент</th>");
        for (String id : sortedTasks) html.append("<th>").append(id).append("</th>");
        html.append("<th>Активность</th><th>ИТОГО</th><th>ОЦЕНКА</th></tr></thead><tbody>");

        for (StudentResult res : results) {
            Map<String, TaskResult> tasks = isPart1 ? res.part1Results() : res.part2Results();
            double total = isPart1 ? res.totalPart1() : res.totalPart2();
            String grade = isPart1 ? res.gradePart1() : res.gradePart2();
            double activity = isPart1 ? res.activityPart1() : res.activityPart2();

            html.append("<tr><td>").append(res.student().fullName()).append("</td>");
            for (String id : sortedTasks) {
                html.append("<td>").append(tasks.getOrDefault(id, TaskResult.failed()).score()).append("</td>");
            }

            String actColor = activity >= 0.7 ? "#28a745" : (activity >= 0.4 ? "#ffc107" : "#dc3545");
            html.append("<td>")
                    .append("<div class='activity-bar-bg'><div class='activity-bar-fill' style='width:")
                    .append((int) (activity * 100)).append("%; background:").append(actColor).append(";'></div></div>")
                    .append("<small>").append(Math.round(activity * 100)).append("%</small></td>");

            html.append("<td class='summary-row'>").append(String.format("%.2f", total)).append("</td>");
            html.append("<td class='grade-cell ok'>").append(grade).append("</td></tr>");
        }
        html.append("</tbody></table></div>");
    }

    private String getTaskSortingKey(String taskId) {
        try {
            String[] parts = taskId.split("_");
            StringBuilder key = new StringBuilder();
            for (int i = 1; i < parts.length; i++) key.append(String.format("%03d", Integer.parseInt(parts[i])));
            return key.toString();
        } catch (Exception e) {
            return taskId;
        }
    }
}
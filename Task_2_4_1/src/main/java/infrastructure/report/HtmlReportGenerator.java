package infrastructure.report;


import core.model.StudentResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HtmlReportGenerator {

    public void generate(List<StudentResult> results, Path outputPath) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<title>Результаты тестирования</title>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; background-color: #f4f7f6; }");
        html.append("table { border-collapse: collapse; width: 100%; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        html.append("th, td { padding: 12px 15px; border: 1px solid #ddd; text-align: left; }");
        html.append("th { background-color: #007bff; color: white; text-transform: uppercase; letter-spacing: 0.03em; }");
        html.append("tr:nth-child(even) { background-color: #f9f9f9; }");
        html.append(".grade-excellent { color: #28a745; font-weight: bold; }");
        html.append(".grade-bad { color: #dc3545; font-weight: bold; }");
        html.append("</style></head><body>");

        html.append("<h1>Сводная ведомость: ООП Java</h1>");
        html.append("<table><thead><tr><th>Студент (GitHub)</th><th>Задачи</th><th>Итог</th><th>Оценка</th></tr></thead><tbody>");

        for (StudentResult res : results) {
            html.append("<tr>");
            html.append("<td><b>").append(res.student().fullName()).append("</b><br><small>")
                    .append(res.student().githubId()).append("</small></td>");

            html.append("<td>");
            res.taskScores().forEach((id, score) ->
                    html.append("<code>").append(id).append("</code>: ").append(score).append("<br>"));
            html.append("</td>");

            html.append("<td>").append(res.totalScore()).append("</td>");

            String gradeClass = res.totalScore() >= 10 ? "grade-excellent" : "grade-bad";
            html.append("<td class='").append(gradeClass).append("'>").append(res.finalGrade()).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody></table></body></html>");

        Files.writeString(outputPath, html.toString());
        System.out.println("Отчет успешно сгенерирован: " + outputPath.toAbsolutePath());
    }
}
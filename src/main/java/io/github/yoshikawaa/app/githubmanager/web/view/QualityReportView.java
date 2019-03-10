package io.github.yoshikawaa.app.githubmanager.web.view;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import io.github.yoshikawaa.app.githubmanager.analysis.CheckboxStatusCounter;
import io.github.yoshikawaa.app.githubmanager.core.web.view.AbstractXlsxWorkSheetView;
import io.github.yoshikawaa.app.githubmanager.entity.Issue;
import io.github.yoshikawaa.app.githubmanager.entity.Label;
import io.github.yoshikawaa.app.githubmanager.entity.report.PullRequestReport;

@Component
public class QualityReportView extends AbstractXlsxWorkSheetView {

    private static final String FILENAME_PREFIX = "quality-report_";

    private static final String[] HEADERS = { "Issue Repo", "Issue No.", "Issue Title", "Milestone", "Type", "PR Repo",
            "PR No.", "PR Title", "State", "CheckBoxes", "Checked", "PR URL" };

    private static final String PREFIX_TYPE = "type:";

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // get model attributes
        String reportName = String.valueOf(model.get("reportName"));
        @SuppressWarnings("unchecked")
        Map<String, MultiValueMap<Issue, PullRequestReport>> reports = (Map<String, MultiValueMap<Issue, PullRequestReport>>) model
                .get("reports");

        // create sheet
        // create work sheet
        Sheet sheet = workbook.createSheet(reportName);

        final AtomicInteger row = new AtomicInteger(0);
        final AtomicInteger cell = new AtomicInteger(0);

        // create header row
        CellStyle style = headerStyle(workbook.createCellStyle(), workbook.createFont());
        Row currentRow = row(sheet, row, cell);
        for (String header : HEADERS) {
            cell(currentRow, cell, style).setCellValue(header);
        }

        // create data rows
        for (Entry<String, MultiValueMap<Issue, PullRequestReport>> repositoryReport : reports.entrySet()) {
            for (Entry<Issue, List<PullRequestReport>> issueReport : repositoryReport.getValue().entrySet()) {
                Issue issue = issueReport.getKey();
                for (PullRequestReport pullRequestreport : issueReport.getValue()) {
                    Issue pull = pullRequestreport.getPull();
                    Map<String, Integer> statusSummary = pullRequestreport.getStatusSummary();

                    style = dataRowStyle(workbook.createCellStyle(), row.get());
                    currentRow = row(sheet, row, cell);
                    if (issue != null) {
                        cell(currentRow, cell, style).setCellValue(issue.getRepo());
                        cell(currentRow, cell, style).setCellValue(issue.getNumber());
                        cell(currentRow, cell, style).setCellValue(issue.getTitle());
                        cell(currentRow, cell, style)
                                .setCellValue((issue.getMilestone() == null) ? null : issue.getMilestone().getTitle());
                        cell(currentRow, cell, style).setCellValue((issue.getMilestone() == null) ? null
                                : String.join(",", issue.getLabels()
                                        .stream()
                                        .map(Label::getName)
                                        .filter(l -> l.startsWith(PREFIX_TYPE))
                                        .collect(Collectors.toList())));
                    }
                    if (pull != null) {
                        cell(currentRow, cell, style).setCellValue(pull.getRepo());
                        cell(currentRow, cell, style).setCellValue(pull.getNumber());
                        cell(currentRow, cell, style).setCellValue(pull.getTitle());
                        cell(currentRow, cell, style).setCellValue(pull.getState());
                        cell(currentRow, cell, style)
                                .setCellValue(statusSummary.get(CheckboxStatusCounter.LABEL_TOTAL));
                        cell(currentRow, cell, style)
                                .setCellValue(statusSummary.get(CheckboxStatusCounter.LABEL_CHECKED));
                        cell(currentRow, cell, style).setCellValue(pull.getHtmlUrl());
                    }
                }
            }
        }

        // auto shaping
        autoSizeAndFilter(sheet, HEADERS.length, row.get());

        // set file name
        setContentDisposition(response, FILENAME_PREFIX + reportName, StandardCharsets.UTF_8);
    }

}

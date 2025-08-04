package com.studyapp.batch;

import com.studyapp.domain.User;
import com.studyapp.dto.WeeklyReport;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ReportBatchConfig {
    
    @Autowired
    private DataSource dataSource;
    
    @Bean
    public Job weeklyReportJob(JobRepository jobRepository, Step weeklyReportStep) {
        return new JobBuilder("weeklyReportJob", jobRepository)
                .start(weeklyReportStep)
                .build();
    }
    
    @Bean
    public Step weeklyReportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("weeklyReportStep", jobRepository)
                .<User, WeeklyReport>chunk(10, transactionManager)
                .reader(userReader())
                .processor(reportProcessor())
                .writer(reportWriter())
                .build();
    }
    
    @Bean
    public ItemReader<User> userReader() {
        return new JdbcCursorItemReaderBuilder<User>()
                .name("userReader")
                .dataSource(dataSource)
                .sql("SELECT id, username, email FROM users")
                .rowMapper(new DataClassRowMapper<>(User.class))
                .build();
    }
    
    @Bean
    public ItemProcessor<User, WeeklyReport> reportProcessor() {
        return user -> {
            // 週次レポートの処理ロジック
            WeeklyReport report = new WeeklyReport();
            report.setUserId(user.getId());
            report.setUsername(user.getUsername());
            report.setReportDate(java.time.LocalDate.now());
            // ここで実際のレポート生成ロジックを実装
            return report;
        };
    }
    
    @Bean
    public ItemWriter<WeeklyReport> reportWriter() {
        return new JdbcBatchItemWriterBuilder<WeeklyReport>()
                .dataSource(dataSource)
                .sql("INSERT INTO weekly_reports (user_id, username, report_date, total_minutes, achievement_rate) " +
                     "VALUES (:userId, :username, :reportDate, :totalMinutes, :achievementRate)")
                .beanMapped()
                .build();
    }
} 
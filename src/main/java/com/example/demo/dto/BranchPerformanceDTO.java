package com.example.demo.dto;

public class BranchPerformanceDTO {
    private String branchName;
    private long apyCount;
    private long pmjjbyCount;
    private long pmsbyCount;
    private long kvpCount;
    private long pmmyCount;
    private long totalScore;
    private int rank;

    public BranchPerformanceDTO(String branchName, long apyCount, long pmjjbyCount, long pmsbyCount, long kvpCount, long pmmyCount) {
        this.branchName = branchName;
        this.apyCount = apyCount;
        this.pmjjbyCount = pmjjbyCount;
        this.pmsbyCount = pmsbyCount;
        this.kvpCount = kvpCount;
        this.pmmyCount = pmmyCount;
        this.totalScore = apyCount + pmjjbyCount + pmsbyCount + kvpCount + pmmyCount;
    }

    // Getters and Setters
    public String getBranchName() { return branchName; }

    public long getApyCount() { return apyCount; }

    public long getPmjjbyCount() { return pmjjbyCount; }

    public long getPmsbyCount() { return pmsbyCount; }

    public long getKvpCount() { return kvpCount; }

    public long getPmmyCount() { return pmmyCount; }

    public long getTotalScore() { return totalScore; }

    public int getRank() { return rank; }

    public void setRank(int rank) { this.rank = rank; }
}

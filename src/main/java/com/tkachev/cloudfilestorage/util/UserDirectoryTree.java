package com.tkachev.cloudfilestorage.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class UserDirectoryTree {
    private String directoryName;
    private String getDirectoryFullPath;
}

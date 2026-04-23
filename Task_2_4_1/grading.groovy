importConfig 'tasks.groovy'

groups {
    group('24214') {
        student id: 'dmObraztsov',
                name: 'Dmitry',
                repo: 'https://github.com/dmObraztsov/OOP.git'
    }
}

extra {
    bonus student: 'dmObraztsov', points: 1.0, reason: 'Activity'
}
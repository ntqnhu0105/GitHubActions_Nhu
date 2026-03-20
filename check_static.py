
import os

files = [
    'demo/src/main/resources/templates/layout.html',
    'demo/src/main/resources/templates/home/home.html'
]

for file_path in files:
    if os.path.exists(file_path):
        print(f"Scanning {file_path}...")
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
            for i, line in enumerate(lines):
                if 'static/' in line:
                    print(f"{i+1}: {line.strip()}")
    else:
        print(f"File not found: {file_path}")

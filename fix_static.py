
import os

files = [
    'demo/src/main/resources/templates/layout.html',
    'demo/src/main/resources/templates/home/home.html'
]

for file_path in files:
    if os.path.exists(file_path):
        print(f"Fixing {file_path}...")
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        new_content = content.replace('src="static/', 'src="/').replace('href="static/', 'href="/')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed {file_path}")
    else:
        print(f"File not found: {file_path}")

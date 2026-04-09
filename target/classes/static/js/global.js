document.addEventListener('DOMContentLoaded', () => {
    // Hide native cursor only if JS successfully executes
    document.body.style.setProperty('cursor', 'none', 'important');
    
    // Check if elements exist, otherwise do nothing
    const dot = document.getElementById('cursor-dot');
    const outline = document.getElementById('cursor-outline');
    const spotlight = document.getElementById('spotlight');

    if (!dot || !outline) return;

    // Attach to all links and interactive elements globally
    const attachHoverTargets = () => {
        const hoverTargets = document.querySelectorAll('a, button, input, select, textarea, .hover-target');
        hoverTargets.forEach(target => {
            // Avoid adding multiple listeners if already added
            if(!target.dataset.hoverAttached) {
                target.dataset.hoverAttached = 'true';
                target.addEventListener('mouseenter', () => {
                    if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.tagName === 'SELECT') {
                        if (dot) dot.classList.add('hidden-cursor');
                        if (outline) outline.classList.add('hidden-cursor');
                    } else {
                        outline.classList.add('hover-effect');
                    }
                    
                    if(target.classList.contains('glass-3d') || target.closest('.glass-3d')) {
                        const glassContainer = target.classList.contains('glass-3d') ? target : target.closest('.glass-3d');
                        glassContainer.classList.add('hovered');
                    }
                });
                target.addEventListener('mouseleave', () => {
                    if (dot) dot.classList.remove('hidden-cursor');
                    if (outline) outline.classList.remove('hidden-cursor');
                    outline.classList.remove('hover-effect');
                    
                    if(target.classList.contains('glass-3d') || target.closest('.glass-3d')) {
                        const glassContainer = target.classList.contains('glass-3d') ? target : target.closest('.glass-3d');
                        glassContainer.classList.remove('hovered');
                    }
                });
            }
        });
    };

    attachHoverTargets();

    // Mouse Move event
    window.addEventListener('mousemove', (e) => {
        const posX = e.clientX;
        const posY = e.clientY;

        // Move cursor dot
        dot.style.left = `${posX}px`;
        dot.style.top = `${posY}px`;
        
        // Smooth outline trailing
        outline.animate({
            left: `${posX}px`,
            top: `${posY}px`
        }, { duration: 500, fill: "forwards" });

        // Update spotlight position
        if (spotlight) {
            spotlight.style.setProperty('--mouse-x', `${posX}px`);
            spotlight.style.setProperty('--mouse-y', `${posY}px`);
        }
        
        // Update 3D card inner glow positions
        document.querySelectorAll('.glass-3d').forEach(card => {
            const rect = card.getBoundingClientRect();
            card.style.setProperty('--mouse-x', `${e.clientX - rect.left}px`);
            card.style.setProperty('--mouse-y', `${e.clientY - rect.top}px`);
        });
    });

    // Handle 3D Tilt globally
    const applyTilt = () => {
        const cards = document.querySelectorAll('.tilt-card:not([data-tilt-attached])');
        cards.forEach(card => {
            card.setAttribute('data-tilt-attached', 'true');
            card.addEventListener('mousemove', (e) => {
                const rect = card.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const y = e.clientY - rect.top;
                const centerX = rect.width / 2;
                const centerY = rect.height / 2;
                
                const rotateX = ((y - centerY) / centerY) * -5;
                const rotateY = ((x - centerX) / centerX) * 5;
                
                card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.01, 1.01, 1.01)`;
            });
            
            card.addEventListener('mouseleave', () => {
                card.style.transform = `perspective(1000px) rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)`;
            });
        });
    };
    applyTilt();

    // Mutation observer to handle elements added dynamically (like HTMX/AJAX or JS filtering)
    const observer = new MutationObserver((mutations) => {
        attachHoverTargets();
        applyTilt();
    });
    
    observer.observe(document.body, { childList: true, subtree: true });
});

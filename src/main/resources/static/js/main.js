(() => {
  const trigger = document.getElementById('how-it-works-btn');
  const overlay = document.getElementById('how-it-works-overlay');
  const closeBtn = document.getElementById('how-it-works-close');
  const iframe = document.getElementById('how-it-works-video');

  if (!trigger || !overlay || !closeBtn || !iframe) return;

  const videoSrc = 'https://www.youtube.com/embed/dQw4w9WgXcQ?autoplay=1';

  function openModal(event) {
    event.preventDefault();
    iframe.src = videoSrc;
    overlay.hidden = false;
  }

  function closeModal() {
    overlay.hidden = true;
    iframe.src = '';
  }

  trigger.addEventListener('click', openModal);
  closeBtn.addEventListener('click', closeModal);
  overlay.addEventListener('click', (event) => {
    if (event.target === overlay) closeModal();
  });
})();

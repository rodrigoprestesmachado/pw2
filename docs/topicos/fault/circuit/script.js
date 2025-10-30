// Circuit Breaker Animation Logic

class CircuitBreaker {
    constructor() {
        this.state = 'CLOSED'; // CLOSED, OPEN, HALF_OPEN
        this.requestCount = 0;
        this.failureCount = 0;
        this.successCount = 0;
        this.requestVolumeThreshold = 10;
        this.failureRatio = 0.5;
        this.openStateDelay = 5000; // 5 seconds
        this.halfOpenAttempts = 0;
        this.maxHalfOpenAttempts = 3;
        
        this.initializeElements();
        this.attachEventListeners();
    }
    
    initializeElements() {
        this.circuitStateEl = document.getElementById('circuit-state');
        this.requestCountEl = document.getElementById('request-count');
        this.failureCountEl = document.getElementById('failure-count');
        this.successRateEl = document.getElementById('success-rate');
        this.failureThresholdEl = document.getElementById('failure-threshold');
        this.failureProgressEl = document.getElementById('failure-progress');
        this.eventLogEl = document.getElementById('event-log');
        this.successBtn = document.getElementById('simulate-success');
        this.failureBtn = document.getElementById('simulate-failure');
    }
    
    attachEventListeners() {
        this.successBtn.addEventListener('click', () => this.simulateRequest(true));
        this.failureBtn.addEventListener('click', () => this.simulateRequest(false));
    }
    
    simulateRequest(isSuccess) {
        if (this.state === 'OPEN') {
            this.logEvent('⚠️ Requisição REJEITADA - Circuit Breaker ABERTO', 'text-orange-600');
            this.circuitStateEl.classList.add('shake-animation');
            setTimeout(() => this.circuitStateEl.classList.remove('shake-animation'), 500);
            return;
        }
        
        this.requestCount++;
        
        if (isSuccess) {
            this.successCount++;
            this.logEvent(`✓ Requisição ${this.requestCount} - SUCESSO`, 'text-emerald-600');
            
            if (this.state === 'HALF_OPEN') {
                this.halfOpenAttempts++;
                if (this.halfOpenAttempts >= this.maxHalfOpenAttempts) {
                    this.closeCircuit();
                }
            }
        } else {
            this.failureCount++;
            this.logEvent(`✗ Requisição ${this.requestCount} - FALHA`, 'text-red-600');
            
            if (this.state === 'HALF_OPEN') {
                this.openCircuit();
            }
        }
        
        this.updateUI();
        this.checkThreshold();
    }
    
    checkThreshold() {
        if (this.requestCount >= this.requestVolumeThreshold) {
            const currentFailureRatio = this.failureCount / this.requestCount;
            
            if (currentFailureRatio >= this.failureRatio && this.state === 'CLOSED') {
                this.openCircuit();
            }
        }
    }
    
    openCircuit() {
        this.state = 'OPEN';
        this.logEvent('🔴 Circuit Breaker ABERTO - Serviço indisponível', 'text-red-600 font-bold');
        this.updateCircuitState();
        
        setTimeout(() => {
            this.halfOpenCircuit();
        }, this.openStateDelay);
    }
    
    halfOpenCircuit() {
        this.state = 'HALF_OPEN';
        this.halfOpenAttempts = 0;
        this.logEvent('🟡 Circuit Breaker HALF-OPEN - Testando recuperação', 'text-yellow-600 font-bold');
        this.updateCircuitState();
    }
    
    closeCircuit() {
        this.state = 'CLOSED';
        this.requestCount = 0;
        this.failureCount = 0;
        this.successCount = 0;
        this.halfOpenAttempts = 0;
        this.logEvent('🟢 Circuit Breaker FECHADO - Serviço recuperado', 'text-emerald-600 font-bold');
        this.updateCircuitState();
        this.updateUI();
    }
    
    updateCircuitState() {
        const stateConfig = {
            'CLOSED': {
                text: 'FECHADO',
                class: 'bg-emerald-500',
                progressColor: 'bg-emerald-500'
            },
            'OPEN': {
                text: 'ABERTO',
                class: 'bg-red-500 pulse-animation',
                progressColor: 'bg-red-500'
            },
            'HALF_OPEN': {
                text: 'HALF-OPEN',
                class: 'bg-yellow-500 pulse-animation',
                progressColor: 'bg-yellow-500'
            }
        };
        
        const config = stateConfig[this.state];
        this.circuitStateEl.textContent = config.text;
        this.circuitStateEl.className = `inline-block px-4 py-2 rounded-full text-white font-medium ${config.class}`;
        
        // Update progress bar color
        this.failureProgressEl.className = `${config.progressColor} h-3 rounded-full transition-all duration-300`;
    }
    
    updateUI() {
        this.requestCountEl.textContent = this.requestCount;
        this.failureCountEl.textContent = this.failureCount;
        
        const successRate = this.requestCount > 0 
            ? Math.round((this.successCount / this.requestCount) * 100)
            : 100;
        this.successRateEl.textContent = `${successRate}%`;
        
        const failuresNeeded = Math.ceil(this.requestVolumeThreshold * this.failureRatio);
        this.failureThresholdEl.textContent = `${this.failureCount}/${failuresNeeded}`;
        
        const progressPercentage = this.requestCount > 0
            ? (this.failureCount / failuresNeeded) * 100
            : 0;
        this.failureProgressEl.style.width = `${Math.min(progressPercentage, 100)}%`;
    }
    
    logEvent(message, className = 'text-gray-600') {
        const timestamp = new Date().toLocaleTimeString('pt-BR');
        const logEntry = document.createElement('div');
        logEntry.className = className;
        logEntry.textContent = `[${timestamp}] ${message}`;
        
        this.eventLogEl.appendChild(logEntry);
        this.eventLogEl.scrollTop = this.eventLogEl.scrollHeight;
        
        // Keep only last 20 entries
        while (this.eventLogEl.children.length > 20) {
            this.eventLogEl.removeChild(this.eventLogEl.firstChild);
        }
    }
}

// Initialize the Circuit Breaker when the page loads
document.addEventListener('DOMContentLoaded', () => {
    new CircuitBreaker();
});
